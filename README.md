# bbs-sample

## ローカル環境での動作確認方法

以下は macOS での動作確認方法です。Windows の場合は適宜読み替えてください。

1. 必要なソフトウェアのインストール
    - OpenJDK 17 ([Amazon Corretto 17](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html))
    - Docker ([Docker Desktop](https://www.docker.com/products/docker-desktop))
    - `docker-compose` コマンド (Docker Desktop に含まれている)
    - `git` コマンド

1. Java 17 にパスを通す
    ```bash
    export JAVA_HOME=`/usr/libexec/java_home -v 17` && \
        PATH=$JAVA_HOME/bin:$PATH && \
        java -version
    ```

1. ローカル環境へ Git リポジトリをクローンする
    ```bash
    git clone "https://github.com/nshmura/bbs-sample.git"
    ```

1. クローンしたリポジトリへ移動する
    ```bash
    cd bbs-sample
    ```

1. サーバービルド
    ```bash
    cd bbs-server

    ./gradlew build

    cd ../
    ```

1. Docker コンテナをビルドする
    ```bash
    docker compose build
    ```

1. Docker コンテナを起動する
    ```bash
    docker compose up
    ```

1. ブラウザで `http://localhost:8000` にアクセスする

1. Docker コンテナを停止、削除する
    ```bash
    docker compose down
    ```

## テストコードの実行方法

1. ローカル環境での動作確認方法の「3. ローカル環境へ Git リポジトリをクローンする」までの手順を実行する
2. `bbs-server` ディレクトリに移動する
    ```bash
    cd bbs-server
    ```
3. テストを実行する
    ```bash
    ./gradlew test
    ```

## ローカルDBの確認方法

```
$ mysql -h 127.0.0.1 -u app -p
Enter password: password

mysql> 
```

## 環境・言語

- サーバ言語: Kotlin
- サーバフレームワーク: Spring Boot
- サーバテンプレートエンジン: Thymeleaf
- データベース: MySQL
