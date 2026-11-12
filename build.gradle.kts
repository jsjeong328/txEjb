buildscript {
    dependencies {
        // 기존 JSch 대신 Ed25519 및 최신 OpenSSH 키를 완벽 지원하는 mwiede 포크 버전 사용
        classpath("com.github.mwiede:jsch:0.2.16")
    }
}

plugins {
    java // Java 플러그인
    war  // WAR 파일 생성을 위한 플러그인
}

group = "com.example"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.servlet:jakarta.servlet-api:6.1.0")
    implementation("jakarta.ejb:jakarta.ejb-api:4.0.1")
    implementation("jakarta.transaction:jakarta.transaction-api:2.0.1")
}

// 1. WAR 파일 생성 설정 (txCaller.war)
tasks.getByName<War>("war") {
    archiveBaseName.set("txCaller")
    archiveVersion.set("")  // 버전 제거
    from("src/main/webapp")
}

// 2. 첫 번째 EJB JAR 태스크 (txBean1.jar)
tasks.register<Jar>("b1") {
    archiveBaseName.set("txBean1")
    archiveVersion.set("")

    from(sourceSets.main.get().output) {
        include("ejb1/*")
        include("caller/*")
        include("constants/*")
        include("helper/*")
        include("logging/*")
    }

    from("src/main/resources/e1") {
        include("ejb-jar.xml", "jeus-ejb-dd.xml", "weblogic-ejb-jar.xml")
        into("META-INF")
    }
}

// 3. 두 번째 EJB JAR 태스크 (txBean2.jar)
tasks.register<Jar>("b2") {
    archiveBaseName.set("txBean2")
    archiveVersion.set("")

    from(sourceSets.main.get().output) {
        include("ejb2/*")
        include("caller/*")
        include("constants/*")
        include("helper/*")
        include("logging/*")
    }

    from("src/main/resources/e2") {
        include("ejb-jar.xml", "jeus-ejb-dd.xml", "weblogic-ejb-jar.xml")
        into("META-INF")
    }
}

// 기본 jar 태스크 비활성화
tasks.jar {
    enabled = false
}

// build 실행 시 b1, b2, war 태스크가 모두 함께 동작하도록 연결
tasks.named("build") {
    dependsOn("b1", "b2")
}

// 4. 개발서버 자동 업로드 태스크 (SCP / JSch / id_ed25519 개인키 연동)
val currentProjectName = rootProject.name

tasks.register("upload") {
    dependsOn("build") // 업로드 전 빌드를 선행 수행합니다.

    doLast {
        val remoteHost = "172.21.200.85"
        val remotePort = 22
        val remoteUser = "jaesik_jeong"
        val privateKeyPath = "C:/Users/정재식(JeongJaesik)/.ssh/id_ed25519"

        val remoteDir = "/home/jaesik_jeong/workspace/testApp/$currentProjectName/"
        val buildLibsDir = layout.buildDirectory.dir("libs").get().asFile

        println("=== 개발서버로 결과물 SCP 업로드 시작 ($remoteHost -> $remoteDir) ===")

        val jsch = com.jcraft.jsch.JSch()
        jsch.addIdentity(privateKeyPath)

        val session = jsch.getSession(remoteUser, remoteHost, remotePort).apply {
            setConfig("StrictHostKeyChecking", "no")
            connect()
        }

        try {
            val channel = session.openChannel("sftp") as com.jcraft.jsch.ChannelSftp
            channel.connect()

            // 원격 디렉토리가 존재하지 않는 경우 상위 경로까지 자동 생성
            try {
                var currentPath = ""
                for (folder in remoteDir.split("/")) {
                    if (folder.isNotEmpty()) {
                        currentPath += "/$folder"
                        try { channel.mkdir(currentPath) } catch (e: Exception) { }
                    }
                }
            } catch (e: Exception) {
            }

            channel.cd(remoteDir)

            // build/libs 내의 빌드 산출물(.war, .jar)을 개발서버로 전송
            buildLibsDir.listFiles()?.forEach { file ->
                if (file.isFile && (file.name.endsWith(".war") || file.name.endsWith(".jar"))) {
                    println("업로드 중: ${file.name} -> $remoteDir")
                    channel.put(file.absolutePath, file.name)
                }
            }

            channel.disconnect()
            println("=== SCP 업로드 완료! ===")
        } finally {
            session.disconnect()
        }
    }
}