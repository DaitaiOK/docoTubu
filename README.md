# どこつぶ (docoTubu)

どこにいてもつぶやける、シンプルなつぶやき投稿Webアプリです。
投稿すると Gemini API と連携した「AI太郎」が自動で返信します。

## 主な機能

- ユーザー登録 / ログイン / ログアウト
- つぶやきの投稿・削除・タイムライン表示（3秒ごとに自動更新）
- AI太郎（Gemini API）による自動返信

## 技術構成

- Java 17 / Spring Boot 3.2（Spring MVC）
- JSP + JSTL
- H2 Database（JDBC + DAOパターン）
- Lombok / Gson
- Gemini API（gemini-2.5-flash）

## セットアップ

### 1. APIキーの設定

`src/main/resources/secret.properties.example` を同じフォルダに `secret.properties` という名前でコピーし、Gemini API キーを記入します。

```properties
gemini.api.key=あなたのAPIキー
```

APIキーは [Google AI Studio](https://aistudio.google.com/apikey) で取得できます。
`secret.properties` が無い場合もアプリは起動しますが、AI太郎は返信しません。

### 2. データベース

H2 のファイルDB（`~/docoTsubu`）を使用します。

- `USERS` テーブルはアプリ起動時に自動作成されます。
- `mutters` テーブルと初期データは `src/main/java/test/InitDB.java` を実行して作成します。

### 3. 起動

**Eclipse（Tomcat 10）の場合**

1. 「ファイル」→「インポート」→「既存 Maven プロジェクト」でこのフォルダを読み込む
2. サーバービューで Tomcat 10 にプロジェクトを追加して起動
3. http://localhost:8080/docoTubu/ にアクセス

**Spring Boot として直接起動する場合**

`DocotubuApplication` を Java アプリケーションとして実行し、http://localhost:8080/ にアクセスします。

## 使い方

1. トップ画面の「ユーザー未登録の方はこちら」からユーザーを登録
2. 登録したユーザーでログイン
3. つぶやきを投稿すると、少し後に AI太郎 が返信します
