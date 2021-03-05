DROP TABLE IF EXISTS `示例`;
CREATE TABLE `示例` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '版本号',
  `num` varchar(32) NOT NULL COMMENT '编号',
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_示例_num` (`num`) USING HASH COMMENT '编码唯一性索引',
  KEY `index_示例_create_date` (`create_date`) USING BTREE COMMENT '创建时间查询索引',
  KEY `index_示例_update_date` (`update_date`) USING BTREE COMMENT '更新时间查询索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='示例';

oauth2_authenticator.sql