plugins {
    java // Java 플러그인
    war // WAR 파일 생성을 위한 플러그인
}

group = "com.example"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    // WAR와 JAR에 필요한 의존성 추가 가능
    implementation(files("/home/jsjeong/workspace/lib/jakarta.ejb-api-4.0.1.jar"))
    implementation(files("/home/jsjeong/workspace/lib/jakarta.servlet-api-6.1.0.jar"))
    implementation(files("/home/jsjeong/workspace/lib/jakarta.transaction-api.jar"))
}

// WAR 파일 생성 설정
tasks.getByName<War>("war") {
    archiveBaseName.set("txCaller") // WAR 파일 이름 설정
    archiveVersion.set("")  // 버전 제거
    from("src/main/webapp") // 추가 리소스 포함
}

// JAR 파일 생성 설정
tasks.getByName<Jar>("jar") {
    archiveBaseName.set("txEJB") // JAR 파일 이름 설정
    archiveVersion.set("")  // 버전 제거
    manifest {
        attributes["Main-Class"] = "" // JAR 파일의 엔트리포인트
    }
}