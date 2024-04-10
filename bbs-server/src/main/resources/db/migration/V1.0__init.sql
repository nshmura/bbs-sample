CREATE TABLE `users`
(
    `id`         VARCHAR(64)  NOT NULL COMMENT 'ユーザID',
    `name`       VARCHAR(255) NOT NULL COMMENT 'ユーザ名',
    `created_at` DATETIME(6) NOT NULL COMMENT '作成日時',
    `updated_at` DATETIME(6) NOT NULL COMMENT '更新日時',
    `deleted_at` DATETIME(6) COMMENT '削除日時',
    PRIMARY KEY (`id`)
);

CREATE TABLE `threads`
(
    `id`         VARCHAR(64)  NOT NULL COMMENT 'スレッドID',
    `title`      VARCHAR(255) NOT NULL COMMENT 'スレッドタイトル',
    `user_id`    VARCHAR(64)  NOT NULL COMMENT 'スレッドを作成したユーザーID',
    `created_at` DATETIME(6) NOT NULL COMMENT '作成日時',
    `updated_at` DATETIME(6) NOT NULL COMMENT '更新日時',
    `deleted_at` DATETIME(6) COMMENT '削除日時',
    PRIMARY KEY (`id`)
);
/*!50110 ALTER TABLE `threads` ADD INDEX idx_updated_at (`updated_at`) */;
/*!50110 ALTER TABLE `threads` ADD INDEX idx_deleted_at_at (`deleted_at`) */;


CREATE TABLE `posts`
(
    `id`         VARCHAR(64) NOT NULL COMMENT '投稿ID',
    `content`    TEXT        NOT NULL COMMENT '投稿本文',
    `user_id`    VARCHAR(64) NOT NULL COMMENT '投稿を作成したユーザーID',
    `thread_id`  VARCHAR(64) NOT NULL COMMENT '投稿が属するスレッドID',
    `created_at` DATETIME(6) NOT NULL COMMENT '作成日時',
    `updated_at` DATETIME(6) NOT NULL COMMENT '更新日時',
    `deleted_at` DATETIME(6) COMMENT '削除日時',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`thread_id`) REFERENCES `threads` (`id`)
);

/*!50110 ALTER TABLE `posts` ADD INDEX idx_created_at (`created_at`) */;
/*!50110 ALTER TABLE `posts` ADD INDEX idx_deleted_at (`deleted_at`) */;

CREATE TABLE `search`
(
    `id`         BIGINT AUTO_INCREMENT NOT NULL,
    `type`       VARCHAR(64) NOT NULL COMMENT '検索対象の種類',
    `message`    TEXT        NOT NULL COMMENT '検索対象本文',
    `user_id`    VARCHAR(64) NOT NULL COMMENT '作成したユーザーID',
    `thread_id`  VARCHAR(64) NOT NULL COMMENT 'スレッドID',
    `post_id`    VARCHAR(64) NOT NULL COMMENT '投稿ID',
    `created_at` DATETIME(6) NOT NULL COMMENT '作成日時',
    `updated_at` DATETIME(6) NOT NULL COMMENT '更新日時',
    PRIMARY KEY (`id`)
);

/*!50110 ALTER TABLE `search` ADD FULLTEXT INDEX idx_fulltext_message (message) WITH PARSER ngram */;
