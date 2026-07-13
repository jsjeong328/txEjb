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