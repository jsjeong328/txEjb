plugins {
    java // Java 플러그인
    war // WAR 파일 생성을 위한 플러그인
}

group = "com.example"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    // WAR와 JAR에 필요한 의존성 추가 가능
    providedCompile("javax:javaee-api:8.0.1")
}

// WAR 파일 생성 설정
tasks.getByName<War>("war") {
    archiveBaseName.set("txCaller") // WAR 파일 이름 설정
    archiveVersion.set("")  // 버전 제거
    from("src/main/webapp") // 추가 리소스 포함
}

// Task to create txBean1.jar
tasks.register<Jar>("b1") {
    archiveBaseName.set("txBean1") // JAR 이름 설정
    archiveVersion.set("") // 버전 제거

    // Java 파일 포함
    from(sourceSets.main.get().output) {
        include("ejb1/*")
        include("caller/*")
        include("constants/*")
        include("helper/*")
        include("logging/*")
    }

    // b1.xml 파일 META-INF에 추가
    from("src/main/resources/e1") {
        include("ejb-jar.xml", "jeus-ejb-dd.xml", "weblogic-ejb-jar.xml")
        into("META-INF") // b1.xml을 META-INF 디렉토리에 넣음
    }
}

// Task to create txBean2.jar
tasks.register<Jar>("b2") {
    archiveBaseName.set("txBean2") // JAR 이름 설정
    archiveVersion.set("") // 버전 제거

    // Java 파일 포함
    from(sourceSets.main.get().output) {
        include("ejb2/*")
        include("caller/*")
        include("constants/*")
        include("helper/*")
        include("logging/*")
    }

    // b1.xml 파일 META-INF에 추가
    from("src/main/resources/e2") {
        include("ejb-jar.xml", "jeus-ejb-dd.xml", "weblogic-ejb-jar.xml")
        into("META-INF") // b1.xml을 META-INF 디렉토리에 넣음
    }
}

// Main jar task 비활성화 (필요한 경우)
tasks.jar {
    enabled = false
}

tasks.register<Exec>("scp") {
    group = "distribution"
    description = "Copies outputs to the dev server via WSL scp."

    // 빌드가 먼저 완료되어야 하므로 의존성 유지
    dependsOn(tasks.named("war"), tasks.named("b1"), tasks.named("b2"))

    // 윈도우 cmd 대신 wsl.exe를 직접 호출하여 리눅스 환경에서 scp를 수행하게 만듭니다.
    // 프로젝트 경로를 WSL이 인식할 수 있도록 파싱합니다.
    val projectDirWsl = projectDir.absolutePath.replace("\\", "/").replace("C:", "/mnt/c")

    commandLine("wsl", "scp", "$projectDirWsl/build/libs/*", "dev:/home/jaesik_jeong/workspace/testApp/")
}