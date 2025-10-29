# [1.0.0-next.4](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/compare/gateway-v1.0.0-next.3...gateway-v1.0.0-next.4) (2025-10-29)


### Bug Fixes

* correct SQL script and repository queries in auth service ([0023898](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/00238980b7fa1ba7a01db59b7b8f140de6948e27))
* improve token validation and add Redis health check ([a67f102](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/a67f10271c9d9991106a46892ae0195cdbf5d097))
* **notification:** improve email verification and template handling ([358f681](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/358f681957f6a0f67b5a510ab46c45f22acd7c7a))


### Features

* add custom exceptions and global error handling entries ([f961385](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/f961385f7b7f45c753033df888809df26ce2c034))
* add login alert email template and notification strategy ([c595604](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/c5956048c5bb82a7ce5e7480b03a5a84de30a80a))
* add RabbitMQ config and custom exception handling ([6e932bc](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/6e932bcc4ca96d975a82f608039bc2ebb61370b7))
* add RabbitMQ listener and environment configuration ([bcdd8c1](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/bcdd8c12bb8d74b0d66f2da2d646c113644db851))
* add resend verification route and improve RabbitMQ config ([fd8b576](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/fd8b576ff929d10ac80b2b0418a1466f22d45cef))
* add trusted devices and login attempt modules ([7fe1f99](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/7fe1f99567eafab26d587c3e52042de417626cee))
* add user-related events, listeners, and async configuration ([6e76e3d](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/6e76e3d0b201c32f8415dfc4b6d1f9592f1d32b4))
* **auth:** add /verify endpoint to AuthController ([ba70a28](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/ba70a28102b7f7e4936af3507b9f1d22f4a9c625))
* **auth:** add access token validation endpoint ([00bb9a5](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/00bb9a5708fb17df8369478b924f054df89019d3))
* **auth:** add ApiErrorFactory and new models for token handling ([402e93f](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/402e93f463e5f7175642fb32a6117c8e65b7687f))
* **auth:** add controller, service, and login/register flow ([ad532af](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/ad532af91b550750194d5cbc6d5d4288b942812e))
* **auth:** add device_id support for tokens and devices ([785e7fb](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/785e7fb056c62829534b5594591c121a58d240ef))
* **auth:** add JWT-based token provider and secure env configuration ([de7d9c0](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/de7d9c06650892cb9a1d1bb40ae47cf2e89608e4))
* **auth:** add logout with token revocation and trusted device management ([9d9830b](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/9d9830b5bf7178ff088481e7d7bf0c9abf9d43e7))
* **auth:** add multi-device logout and revoke logic ([23075f4](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/23075f475a398db8df84a3101515c71436d6eccc))
* **auth:** add password reset flow and related endpoints ([286c16c](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/286c16cb57d2993b559e3e1b9796ec5708983cbb))
* **auth:** add password security validations ([edf2d80](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/edf2d80b13e5b0b6c9a1f4a09d7b47c60e248b78))
* **auth:** add refresh token entity, service, and token provider ([09ef9e9](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/09ef9e998edce87c9d6233c1ca6ca2445f1bdd75))
* **auth:** add resend verification email and improve consistency ([9e6f239](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/9e6f239879df509d40c4377f4c4611ad0e8df476))
* **auth:** add role domain/service layer and improve DTO handling ([76b650a](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/76b650a03902171f70d0afcbe6295d7edf1479fa))
* **auth:** embed device_id in JWT and update services ([a387ede](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/a387ede9449b53feec2e85e8051ac0c08aaad548))
* **auth:** improve notification and role handling in user registration ([24f0912](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/24f0912de80b6bb8d4a5f6bedc300c7ce9bee224))
* **auth:** publish password reset events in service ([75b1429](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/75b14293fd70e510e9b15c405013c020d94fdd2a))
* **auth:** update init.sql and add /me endpoint implementation ([f83a23e](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/f83a23e1e2669f4e141c36a72c8a4e27617135b3))
* **auth:** use SHA-256 for token hashing and add validation check ([746fb1e](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/746fb1e7792d987cf56889a6f268c2d0b59734a8))
* **config:** add password reset and support email vars ([0685914](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/06859144dfbe42695de940dc7efc0e5a5875e9af))
* **core:** add entities, repos, mailhog svc, db updates & seeds ([87be83c](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/87be83c3d71cf7be98dad10eac7828ee8031efe0))
* **db:** add soft delete and audit timestamps ([9751fb3](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/9751fb3c5ae553a2dfbc87aadbf7b3c48d42914b))
* **email:** add password reset templates and strategies ([6122c9f](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/6122c9f4d9dfa2e6d17a5e2476e0d7efb2ccc3f1))
* **email:** add welcome user strategy and update verification template ([ce91619](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/ce916196c81aa68dd3b323a4be9f910a387a588c))
* enhance TokenProvider with user extraction and validation ([f3a5152](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/f3a51528d792d9a00a6649f14f0bcb0a86b544cd))
* **events:** add password reset request and success handlers ([679b300](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/679b300b20d9fd16d13f87f2b63b5044f8310821))
* extend refresh token service with rotation and validation ([3a8e7fa](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/3a8e7faed0f108da6242f185eb51d88491c8997e))
* **gateway:** add ApiErrorFactory and update token validation ([e71c018](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/e71c018498aece166d1d58df8f3200e346cf0876))
* **gateway:** add swagger aggregator configuration ([51b3124](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/51b31249ec0a687f4ac66dcea4c1f44a98402f82))
* implement login and refresh flow with event delegation ([397f3fd](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/397f3fdb9ea2adde219e00516d2ad9a031e58d9c))
* **password-reset:** add expiration and notification types ([6ab7a55](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/6ab7a55cba3fee7acbb719e6a53592daa64d99de))
* **security:** add JWT validation layer in gateway ([c86fcac](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/c86fcacfd9f59a7df9d15b68f8839792235f14ba))
* **security:** add modelmapper, global handler, and validations ([c8e1dcc](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/c8e1dcc38ada8e6787ca9e60db3708906044d71e))
* update RabbitMQ JSON handling and DLQ in notification service ([61b1798](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/61b1798af546a9ee4adccf8c8b172ac53db7b25f))
* **user:** simplify role model and add user service + DTOs ([8054cd6](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/8054cd61eacbda01b1c9004ee684a49605e37b6f))
* **utils:** add request helpers and value objects ([378c8cc](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/378c8cc3e5d52efc1e51eca186dee96f5293745d))

# [1.0.0-next.3](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/compare/gateway-v1.0.0-next.2...gateway-v1.0.0-next.3) (2025-10-09)


### Bug Fixes

* **sql:** update init scripts to remove cross-schema references ([deebe81](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/deebe81a8c048783fd82fa2f4b4967c3ad6ed1ac))


### Features

* add DTOs and controllers for notifications and mail testing ([4ac0f39](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/4ac0f3953b50bc891f5b067ea0bd09784565a421))
* add dummy Notification repository placeholder ([c9f064c](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/c9f064c1a38e4268a4187ff4326c69c95862c069))
* add HTML email templates for notifications ([a1a37ae](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/a1a37ae5c164492ea2b6c8fb182b0da2a5076242))
* add Lombok, SendGrid, Thymeleaf, Mail starter and Jakarta validations ([0a46200](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/0a46200fc60407b6c1958d22527715d046db1c92))
* add mail service with SMTP, SendGrid and FakeMail implementations ([4c71c48](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/4c71c4886ba5c6f578eab97016efdc830b9b458f))
* add notification channel and email strategy classes ([4efaf85](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/4efaf85c84efb7d06d6051b153880eb903a9252a))
* add Notification entity and enums for channel and type ([39a2e25](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/39a2e259a53c5860146abb548860597036420b93))
* add notification, email and template services with impl ([abe685a](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/abe685a54d4847d2ad92e94efcfb488a6b1ea301))
* **db:** full schema redesign with timestamps and soft delete ([a21fa79](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/a21fa79a5be807ba78a27a9788b71971608c9697))
* **feat(swagger:** configure aggregated OpenAPI docs via gateway ([09fe5d9](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/09fe5d9db9947a73d1976cc0fe4764f6d7a9b2a4))
* **lint:** allow constant exports in react-refresh rule ([794328a](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/794328acccf8c695ef8106182c03d154c7c74cbb))
* **pwa:** add Progressive Web App support with manifest, icons, and service worker configuration ([9277794](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/9277794c05cf239c039c219e5f9d8e54c5366aaa))

# [1.0.0-next.2](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/compare/gateway-v1.0.0-next.1...gateway-v1.0.0-next.2) (2025-09-28)


### Features

* **migrations:** add V1 initial migration per service ([29e7b24](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/29e7b24b7c431ba67b0fa8273ef5f5f053366659))
* **orders:** scaffold orders service and initial schema ([3320b87](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/3320b879154de19755d10e2cfc2717f637f9dcca))

# 1.0.0-next.1 (2025-09-25)


### Features

* create health controller in each service ([5242aa8](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/5242aa84c2acd4e08cc626e18850151eb496d694))
* create health controller in each service ([1915f18](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/1915f18d100d4c059a2f79cb0d8ead7aad19177e))
* initial folder structure ([77b0cda](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/77b0cda5bd0bfeb7f684bdc382377dac8c8ffca5))
* initial folder structure ([1b6ab44](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/1b6ab44ed18c104ab9d68c5993a02007bfcbad20))
* initial folder structure ([c43e40c](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/c43e40ca1a82fe5a6b73caa20e6a5ce6bf6ea1ce))
* initial folder structure ([1e5351f](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/1e5351febef8368dd87b3b38b506da117d933849))
* initial folder structure ([eb345a7](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/eb345a71038110755d0efb845f1c63086e554fbe))
* initial folder structure ([eb32b0d](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/eb32b0ddf503dc429b2bc589d5c5c628a4d41794))
* initial folder structure ([09476c0](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/09476c064885806721f767d7b86b14120110ab55))
* initial folder structure ([71e8db5](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/71e8db594a25af3ba0671aacf5a10b80523e8ad8))
* initial folder structure ([a77f5ad](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/a77f5ad414aa1eada44588e2bcb20e1a703ffdfd))
* initial notification folder structure ([869b865](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/869b8659ffff2239190481aba3c3c7fc37eae580))
* initial notification folder structure ([74ff708](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/74ff708eacaef52c8a819630c5b7be5847cef5c3))
* more folder structure ([f5ed21b](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/f5ed21b0b3da90cba5d11843ee7c81c089b3108e))
* more folder structure ([f71bec3](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/f71bec39f1774f479d3cfd3c5c504c058b4864cb))
* more folder structure ([6a1794c](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/6a1794c711432c83fd2c915a9d788cf9f81fe940))
* more folder structure ([efd7f30](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/efd7f3032abe5433e0ec38ad4fe997bd4aa4e6a5))
* Spring Initializr ([5db24d7](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/5db24d70692e3e8cd9c675ed859e4ddf11eb0b86))
* Spring Initializr ([c830523](https://github.com/405392-Manca-Wysocki-Elias-Lautaro/ticketera/commit/c83052399e37390cfc0e19617215479af263351d))
