CREATE TABLE IF NOT EXISTS `yjp_namespace` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `sys` VARCHAR(100) NOT NULL,   -- 所属系统
  `database` VARCHAR(100) NOT NULL,   -- database名称
  `version` VARCHAR(20) NOT NULL,    -- 版本号
  `database_id` BIGINT NOT NULL,   -- database id
  `active` TINYINT(1) NOT NULL,  -- 版本是否启用
  `desc` VARCHAR(100) NOT NULL,  --注释
  `create_time` TIMESTAMP NOT NULL DEFAULT '1970-01-01 08:00:01',
  `create_by` BIGINT NOT NULL,
  `update_time` TIMESTAMP NOT NULL DEFAULT '1970-01-01 08:00:01',
  `update_by` BIGINT NOT NULL,
  PRIMARY KEY (`id`)
  )
ENGINE = InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;



CREATE TABLE IF NOT EXISTS `yjp_table` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `database_id` BIGINT NOT NULL,   -- database id
  `table` VARCHAR(100) NOT NULL,   -- table名称
  `isSide` TINYINT(1) NOT NULL,  -- table为维表是source表
  `type` VARCHAR(100) NOT NULL,  --当表为维表时 类型是mysql还是kudu或其他
  `primary_key` VARCHAR(100) NOT NULL, --当表为维表时 说明维表的主键
  `table` VARCHAR(100) NOT NULL,   -- table名称
  `version` VARCHAR(20) NOT NULL,    -- 版本号
  `table_id` BIGINT NOT NULL,   -- table id
  `address` VARCHAR(100) NOT NULL, --table 地址  如Kafka地址
  `active` TINYINT(1) NOT NULL,  -- 版本是否启用
  `desc` VARCHAR(100) NOT NULL,  --注释
  `create_time` TIMESTAMP NOT NULL DEFAULT '1970-01-01 08:00:01',
  `create_by` BIGINT NOT NULL,
  `update_time` TIMESTAMP NOT NULL DEFAULT '1970-01-01 08:00:01',
  `update_by` BIGINT NOT NULL,
  PRIMARY KEY (`id`)
  )
ENGINE = InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE IF NOT EXISTS `yjp_column` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `table_id` BIGINT NOT NULL,   -- table id
  `column` VARCHAR(100) NOT NULL,   -- column名称
  `type` VARCHAR(100) NOT NULL,  --字段类型
  `version` VARCHAR(20) NOT NULL,    -- 版本号
  `column_id` BIGINT NOT NULL,   -- column id
  `active` TINYINT(1) NOT NULL,  -- 版本是否启用
  `desc` VARCHAR(100) NOT NULL,  --注释
  `create_time` TIMESTAMP NOT NULL DEFAULT '1970-01-01 08:00:01',
  `create_by` BIGINT NOT NULL,
  `update_time` TIMESTAMP NOT NULL DEFAULT '1970-01-01 08:00:01',
  `update_by` BIGINT NOT NULL,
  PRIMARY KEY (`id`)
  )
ENGINE = InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

