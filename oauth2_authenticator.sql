

-- ----------------------------
-- Table structure for oauth_access_token
-- ----------------------------
DROP TABLE IF EXISTS `oauth_access_token`;
CREATE TABLE `oauth_access_token`  (
  `authentication_id` varchar(255) NOT NULL,
  `token_id` varchar(255) NULL DEFAULT NULL,
  `token` blob NULL,
  `user_name` varchar(255) NULL DEFAULT NULL,
  `client_id` varchar(255) NULL DEFAULT NULL,
  `authentication` blob NULL,
  `refresh_token` varchar(255) NULL DEFAULT NULL,
  PRIMARY KEY (`authentication_id`)
) ENGINE = InnoDB;

-- ----------------------------
-- Table structure for oauth_approvals
-- ----------------------------
DROP TABLE IF EXISTS `oauth_approvals`;
CREATE TABLE `oauth_approvals`  (
  `userId` varchar(255) NULL DEFAULT NULL,
  `clientId` varchar(255) NULL DEFAULT NULL,
  `scope` varchar(255) NULL DEFAULT NULL,
  `status` varchar(10) NULL DEFAULT NULL,
  `expiresAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastModifiedAt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
) ENGINE = InnoDB;

-- ----------------------------
-- Table structure for oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS oauth_client_details;
CREATE TABLE `oauth_client_details` (
  `client_id` varchar(64) NOT NULL COMMENT '编号',
  `resource_ids` varchar(256) DEFAULT NULL COMMENT '资源ID',
  `client_secret` varchar(256) DEFAULT NULL COMMENT '密钥',
  `scope` varchar(256) DEFAULT NULL COMMENT '域',
  `authorized_grant_types` varchar(256) DEFAULT NULL COMMENT '授权模式',
  `web_server_redirect_uri` varchar(256) DEFAULT NULL COMMENT '回调地址',
  `authorities` varchar(256) DEFAULT NULL COMMENT '权限',
  `access_token_validity` int(11) DEFAULT NULL COMMENT '请求令牌时间',
  `refresh_token_validity` int(11) DEFAULT NULL COMMENT '刷新令牌时间',
  `additional_information` varchar(4096) DEFAULT NULL COMMENT '扩展信息',
  `autoapprove` varchar(256) DEFAULT NULL COMMENT '自动放行',
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户端配置表';

-- ----------------------------
-- Table structure for oauth_client_token
-- ----------------------------
DROP TABLE IF EXISTS `oauth_client_token`;
CREATE TABLE `oauth_client_token`  (
  `token_id` varchar(255) NULL DEFAULT NULL,
  `token` blob NULL,
  `authentication_id` varchar(255) NOT NULL,
  `user_name` varchar(255) NULL DEFAULT NULL,
  `client_id` varchar(255) NULL DEFAULT NULL,
  PRIMARY KEY (`authentication_id`)
) ENGINE = InnoDB;

-- ----------------------------
-- Table structure for oauth_code
-- ----------------------------
DROP TABLE IF EXISTS `oauth_code`;
CREATE TABLE `oauth_code`  (
  `code` varchar(255) NULL DEFAULT NULL,
  `authentication` blob NULL
) ENGINE = InnoDB;

-- ----------------------------
-- Table structure for oauth_refresh_token
-- ----------------------------
DROP TABLE IF EXISTS `oauth_refresh_token`;
CREATE TABLE `oauth_refresh_token`  (
  `token_id` varchar(255) NULL DEFAULT NULL,
  `token` blob NULL,
  `authentication` blob NULL
) ENGINE = InnoDB;
