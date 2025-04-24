alter session set container = XEPDB1;

-- SETTING CONFIG
-- Update with your schema name
ALTER SESSION SET CURRENT_SCHEMA = SMOOTHNESS_OWNER;

-- REQUIRED: Admin Config
insert into SETTING (KEY, VALUE, TYPE, DESCRIPTION, TAG, WEIGHT) values ('ADMIN_ROLE_NAME', 'smoothness-demo-admin', 'STRING', 'App-specific Admin Role Name', 'AUTH', 1);

-- REQUIRED: CDN Config
insert into SETTING (KEY, VALUE, TYPE, DESCRIPTION, TAG, WEIGHT) values ('SMOOTHNESS_CDN_ENABLED', 'N', 'BOOLEAN', 'Smoothness weblib resources from CDN.  Defaults to No = serve files locally. CDN is for minified/combined files on shared Content Delivery Network (CDN) server - Nice for when multiple apps use same resources to have warm cache.', 'CDN', 1);
insert into SETTING (KEY, VALUE, TYPE, DESCRIPTION, TAG, WEIGHT) values ('SMOOTHNESS_SERVER', 'ace.jlab.org/cdn', 'STRING', 'Host name and port of content delivery network host for shared smoothness resources. Only used if SMOOTHNESS_CDN_ENABLED = Y', 'CDN', 2);

-- OPTIONAL: IP READ FILTER (ACL)
insert into SETTING (KEY, VALUE, TYPE, DESCRIPTION, TAG, WEIGHT) values ('IP_READ_FILTER_ENABLED', 'N', 'BOOLEAN', 'Whether to enable IP filtering of pages requiring auth to view.  You must redeploy the app for this setting change to take effect.', 'ACL', 1);
insert into SETTING (KEY, VALUE, TYPE, DESCRIPTION, TAG, WEIGHT, CHANGE_ACTION_JNDI) values ('IP_READ_ALLOWLIST_PATTERN', '127\.0.*', 'STRING', 'Java REGEX Pattern of allowed IPs for unauthenticated access to view IpReadFilter pages', 'ACL', 2, 'java:global/sim/IpReadAllowlistReconfigureService');
insert into SETTING (KEY, VALUE, TYPE, DESCRIPTION, TAG, WEIGHT) values ('IP_READ_URL_PATTERN', '/*', 'STRING', 'URL patterns to match when applying the IP Read Filter.  You must redeploy the app for this setting change to take effect.', 'ACL', 3);
