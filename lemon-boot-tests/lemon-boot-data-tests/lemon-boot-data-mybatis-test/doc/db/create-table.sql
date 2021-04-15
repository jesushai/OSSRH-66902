/*
 测试的表
 */

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for base_region
-- ----------------------------
DROP TABLE IF EXISTS `base_region`;
CREATE TABLE `base_region`  (
    `id_` bigint(20) NOT NULL,
    `pid_` bigint(20) NULL DEFAULT NULL,
    `name_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
    `type_` smallint(6) NULL DEFAULT NULL,
    `code_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
    PRIMARY KEY (`id_`) USING BTREE
    ) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for base_brand
-- ----------------------------
DROP TABLE IF EXISTS `base_brand`;
CREATE TABLE `base_brand`  (
  `id_` int(11) NOT NULL AUTO_INCREMENT,
  `name_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '品牌商名称',
  `desc_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '品牌商简介',
  `pic_url_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '品牌商页的品牌商图片',
  `sort_order_` tinyint(3) NULL DEFAULT 50,
  `floor_price_` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '品牌商的商品低价，仅用于页面展示',
  `add_time_` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time_` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted_` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `tenant_` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id_`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1046003 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '品牌商表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of base_brand
-- ----------------------------
INSERT INTO `base_brand` VALUES (1001000, 'MUJI制造商', '严选精选了MUJI制造商和生产原料，\n用几乎零利润的价格，剔除品牌溢价，\n让用户享受原品牌的品质生活。', 'http://yanxuan.nosdn.127.net/1541445967645114dd75f6b0edc4762d.png', 2, 12.90, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1001002, '内野制造商', '严选从世界各地挑选毛巾，最终选择了为日本内野代工的工厂，追求毛巾的柔软度与功能性。品质比肩商场几百元的毛巾。', 'http://yanxuan.nosdn.127.net/8ca3ce091504f8aa1fba3fdbb7a6e351.png', 10, 29.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1001003, 'Adidas制造商', '严选找到为Adidas等品牌制造商，\n选取优质原材料，与厂方一起设计，\n为你提供好的理想的运动装备。', 'http://yanxuan.nosdn.127.net/335334d0deaff6dc3376334822ab3a2f.png', 30, 49.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1001007, '优衣库制造商', '严选找到日本知名服装UNIQLO的制造商，\n选取优质长绒棉和精梳工艺，\n与厂方一起设计，为你提供理想的棉袜。', 'http://yanxuan.nosdn.127.net/0d72832e37e7e3ea391b519abbbc95a3.png', 12, 29.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1001008, '膳魔师制造商', '严选深入保温行业内部，\n找到德国膳魔师制造商的代工厂。\n同样的品质，却有更优的价格。', 'http://yanxuan.nosdn.127.net/5fd51e29b9459dae7df8040c8219f241.png', 40, 45.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1001010, '星巴克制造商', '严选寻访全国保温杯制造企业，\n最终找到高端咖啡品牌星巴克的制造商，\n专注保温杯生产20年，品质与颜值兼备。', 'http://yanxuan.nosdn.127.net/5668bc50f2f2e551891044525710dc84.png', 34, 39.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1001012, 'Wedgwood制造商', '严选寻访英国皇室御用陶瓷Wedgwood制造商，\n制模到成品，历经25道工序、7次检验、3次烧制，\n你看不见的地方，我们也在坚持。', 'http://yanxuan.nosdn.127.net/68940e8e23f96dbeb3548d943d83d5e4.png', 21, 39.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1001013, 'Royal Doulton制造商', '严选深入英国最大骨瓷品牌Royal Doulton制造商， \n顶级英国瓷器的代名词，广受世界皇室喜爱。\n每件瓷器，都有自己的故事。', 'http://yanxuan.nosdn.127.net/0de643a02043fd9680b11e21c452adaa.png', 47, 24.90, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1001015, '日本KEYUCA制造商', 'KEYUCA是日本餐具及料理用具品牌，\n遵循极简原木风，高端餐具体验。\n严选的餐具正是来自这一品牌制造商。', 'http://yanxuan.nosdn.127.net/9b85b45f23da558be101dbcc273b1d6d.png', 49, 14.90, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1001016, '爱慕制造商', '150家样品比对筛选，20家工厂深入走访，\n严选最终选定高端内衣爱慕制造商，\n20年品质保证，为你打造天然舒适的衣物。', 'http://yanxuan.nosdn.127.net/5104f84110eac111968c63c18ebd62c0.png', 9, 35.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1001020, 'Ralph Lauren制造商', '我们与Ralph Lauren Home的制造商成功接洽，掌握先进的生产设备，传承品牌工艺和工序。追求生活品质的你，值得拥有。', 'http://yanxuan.nosdn.127.net/9df78eb751eae2546bd3ee7e61c9b854.png', 20, 29.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1001037, '新秀丽制造商', '严选为制作品质与颜值兼具的箱包，\n选定新秀丽、CK、Ricardo等品牌合作的制造商，\n拥有国内先进流水线20余条，实力保障品质。', 'http://yanxuan.nosdn.127.net/80dce660938931956ee9a3a2b111bd37.jpg', 5, 59.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1001038, 'Coach制造商', '严选为制作高品质高颜值皮具配件，\n由Coach、MK等品牌制造商生产，\n由严选360度全程监制，给你带来优质皮具。', 'http://yanxuan.nosdn.127.net/1b1cc16135fd8467d40983f75f644127.png', 3, 49.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, 100);
INSERT INTO `base_brand` VALUES (1001039, 'MK制造商', '严选为制造高品质的皮具，\n选择Michael Kors品牌合作的制造工厂，\n18年专业皮具生产经验，手工至美，品质保证。', 'http://yanxuan.nosdn.127.net/fc9cd1309374f7707855de80522fb310.jpg', 17, 79.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1001045, '罗莱制造商', '严选团队为打造吸湿透气柔软的蚕丝被，\n从蚕茧原材到温感性能，多次甄选测试\n选择罗莱制造商工厂，手工处理，优质舒适。', 'http://yanxuan.nosdn.127.net/14122a41a4985d23e1a172302ee818e9.png', 45, 699.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1003000, 'Carters制造商', '来自Carters大牌代工厂生产，\n严选纯天然材料，无荧光不添加，\nITS安心标志权威检测，安全护航。', 'http://yanxuan.nosdn.127.net/efe9131599ced0297213e6ec67eb2174.png', 41, 19.90, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, 100);
INSERT INTO `base_brand` VALUES (1005001, 'Goody制造商', '严选深入美国百年发饰品牌Goody制造商，\n确保每把梳子做工精湛，养护头皮。\n戴安娜王妃的最爱，你也能拥有。', 'http://yanxuan.nosdn.127.net/7c918f37de108f3687d69b39daab34eb.png', 48, 39.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1006000, '范思哲制造商', '严选找寻意大利奢侈品牌范思哲Versace的制造商，\n致力于为用户带来精致、优雅、时尚的皮包，\n传承独特美感，体验品质生活。', 'http://yanxuan.nosdn.127.net/c80ae035387495a61a4515906205efff.png', 18, 99.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1008000, 'WPC制造商', '严选寻找日本雨伞品牌W.P.C制造商，\n采用严谨工艺以及环保材料，\n沉淀15年行业经验，打造精致雨具。', 'http://yanxuan.nosdn.127.net/c4e97cc87186ce17f9316f3ba39e220c.png', 22, 59.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1010001, '竹宝堂制造商', '严选走访河北、安徽等制刷基地，\n选定竹宝堂、丝芙兰等品牌的制造商，\n严格把关生产与质检，与您一同追求美的生活。', 'http://yanxuan.nosdn.127.net/61b0b7ae4f0163422009defbceaa41ad.jpg', 39, 29.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1010002, '资生堂制造商', '发现美，成为美，是女性一生的追求。\n严选找寻资生堂代工厂，打造天然美妆产品，\n致力于带来更多美的体验和享受。', 'http://yanxuan.nosdn.127.net/5449236b80d1e678dedee2f626cd67c4.png', 19, 29.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1015000, 'NITORI制造商', '宠物是人类最温情的陪伴，\n严选找寻日本最大家居品牌NITORI制造商，\n每一个脚印，都是为了更好地关怀你的TA', 'http://yanxuan.nosdn.127.net/6f3d310601b18610553c675e0e14d107.png', 43, 69.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1016002, 'HUGO BOSS制造商', '严选深入德国知名奢侈品HUGO BOSS的制造商，\n开发睡衣、睡袍、休闲裤等轻奢品质家居服，\n希望你在家的每一天都优雅精致。', 'http://yanxuan.nosdn.127.net/70ada9877b2efa82227437af3231fe50.png', 11, 45.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1018000, 'Sperry制造商', '严选团队对比多家硫化鞋制造商产品质量，\n走访多个制鞋工厂，最终选定Sperry品牌制造商，\n为你提供一双舒适有型的高品质帆布鞋。', 'http://yanxuan.nosdn.127.net/2eb12d84037346441088267432da31c4.png', 32, 199.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1021000, 'Marc Jacobs制造商', '严选寻访独立设计品牌Marc Jacobs的制造商，\n严格选材，细究纺织与生产的细节，多次打磨，\n初心不忘，为你带来优雅高档的服饰配件。', 'http://yanxuan.nosdn.127.net/c8dac4eb1a458d778420ba520edab3d0.png', 24, 69.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1022000, 'UGG制造商', '为寻找优质的皮毛一体雪地靴，\n严选走访多家雪地靴制造商，对比工艺，\n甄选UGG认可的代工厂，只为足下的优雅舒适。', 'http://yanxuan.nosdn.127.net/4d2a3dea7e0172ae48e8161f04cfa045.jpg', 29, 59.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1022001, 'Palladium制造商', '严选探访多个制鞋大厂，选定Palladium制造商，\n对比工艺选材，找到传承多年的制鞋配方，\n只为制作一款高品质休闲鞋。', 'http://yanxuan.nosdn.127.net/3480f2a4026c60eb4921f0aa3facbde8.png', 31, 249.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1023000, 'PetitBateau小帆船制造商', '为打造适合宝宝的婴童服装，\n严选团队寻找PetitBateau小帆船的品牌制造商，\n无荧光剂，国家A类标准，让宝宝穿的放心。', 'http://yanxuan.nosdn.127.net/1a11438598f1bb52b1741e123b523cb5.jpg', 25, 36.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1024000, 'WMF制造商', '严选找寻德国百年高端厨具WMF的制造商，\n选择拥有14年经验的不锈钢生产工厂，\n为你甄选事半功倍的优质厨具。', 'http://yanxuan.nosdn.127.net/2018e9ac91ec37d9aaf437a1fd5d7070.png', 8, 9.90, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1024001, 'OBH制造商', '严选寻找OBH品牌的制造商，打造精致厨具，\n韩国独资工厂制造，严格质检，品质雕琢\n力求为消费者带来全新的烹饪体验。', 'http://yanxuan.nosdn.127.net/bf3499ac17a11ffb9bb7caa47ebef2dd.png', 42, 39.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1024003, 'Stoneline制造商', '严选找寻德国经典品牌Stoneline的制造商，\n追踪工艺，考量细节，亲自试用，\n为你甄选出最合心意的锅具和陶瓷刀，下厨如神。', 'http://yanxuan.nosdn.127.net/3a44ae7db86f3f9b6e542720c54cc349.png', 28, 9.90, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1024006, 'KitchenAid制造商', '严选寻访KitchenAid品牌的制造商，\n采用德国LFGB认证食品级专用不锈钢，\n欧式简约设计，可靠安心，尽享下厨乐趣。', 'http://yanxuan.nosdn.127.net/e11385bf29d1b3949435b80fcd000948.png', 46, 98.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1025000, 'Timberland制造商', '为制作优质时尚的工装鞋靴，\n严选团队深入探访国内外制靴大厂，选择Timberland制造商，\n工厂拥有15年制鞋历史，专业品质有保证。', 'http://yanxuan.nosdn.127.net/6dcadb0791b33aa9fd00380b44fa6645.png', 37, 359.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1025001, 'Kenneth Cole制造商', '为出品优质格调的商务鞋靴，\n严选团队选择Kenneth Cole品牌合作的制造商，\n一切努力，只为打造高品质鞋靴。', 'http://yanxuan.nosdn.127.net/236322546c6860e1662ab147d6b0ba2f.jpg', 7, 349.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1026000, 'CK制造商', '严选寻访Calvin Klein品牌的制造商，\n深入世界领带第一生产地，设计与品质并重，\n致力于给消费者带来优质典雅的服饰用品。', 'http://yanxuan.nosdn.127.net/658f09b7ec522d31742b47b914d64338.png', 1, 39.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1026001, 'Under Armour制造商', '严选为甄选优质好袜，走访东北、新疆等产袜基地，\n最终选定Under Armour品牌的合作制造商，\n从原料、工艺、品质多维度筛选监制，保证好品质。', 'http://yanxuan.nosdn.127.net/4e93ea29b1d06fabfd24ba68a9b20a34.jpg', 35, 39.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1028000, 'Gucci制造商', '严选为设计一款优雅时尚的品质礼帽，\n找寻拥有10来年经验的大型毛毡帽厂商合作，\n坚持打造好设计、好工艺、好材质的潮流礼帽。', 'http://yanxuan.nosdn.127.net/278869cad9bf5411ffc18982686b88fb.jpg', 23, 59.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1028003, 'Burberry制造商', '为打造时尚舒适的童装系列，\n严选选择Burberry制造商，优化版型配色\n英伦风情融入经典格纹，百搭优雅气质款。', 'http://yanxuan.nosdn.127.net/07af01e281c7e0b912d162d611e22c32.jpg', 4, 99.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1033003, 'Armani制造商', '严选团队携手国际标准化专业生产厂家，\n厂家长期为Armani、Alexander wang等知名品牌代工，\n专业进口设备，精密质量把控，精于品质居家体验。', 'http://yanxuan.nosdn.127.net/981e06f0f46f5f1f041d7de3dd3202e6.jpg', 26, 199.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1033004, '爱马仕集团制造商', '严选采用欧洲一线品牌爱马仕的御用香料供应商，\n经过反复配比改良、试香调香、选品定样，\n为你带来独特馥郁的散香体验。', 'http://yanxuan.nosdn.127.net/d98470dd728fb5a91f7aceade07572b5.png', 33, 19.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1034001, 'Alexander McQueen制造商', '为制造精致实用的高品质包包，\n严选团队选择Alexander McQueen制造商，\n严格筛选，带来轻奢优雅体验。', 'http://yanxuan.nosdn.127.net/db7ee9667d84cbce573688297586699c.jpg', 16, 69.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1037000, '厚木ATSUGI制造商', '严选考究袜子品质，层层把关原料生产，\n携手12年行业生产资质的厚木品牌制造商，\n带来轻盈优雅，舒适显瘦的袜子系列。', 'http://yanxuan.nosdn.127.net/7df55c408dbac6085ed6c30836c828ac.jpg', 27, 29.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1038000, 'Birkenstock集团制造商', '为打造一双舒适的软木拖鞋，\n严选团队寻找BIRKENSTOCK集团旗下产品制造商，\n360度全程监制，舒适随脚，百搭文艺。', 'http://yanxuan.nosdn.127.net/05a2ecffb60b77e4c165bd8492e5c22a.jpg', 14, 59.90, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1038001, 'Nine West制造商', '为打造一双优雅舒适的高跟鞋，\n严选选择美国Nine West玖熙品牌的制造商，\n让美丽绽放在足尖。', 'http://yanxuan.nosdn.127.net/ad4df7848ce450f00483c2d5e9f2bfa7.png', 13, 219.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1039000, 'TEFAL制造商', '严选对标国际品质，致力于高品质生活好物，\n执着寻求优质厨房电器供应商，\n携手WMF、Tefal制造商，打造高品质厨具。', 'http://yanxuan.nosdn.127.net/2b7a07e25a3f3be886a7fb90ba975bb7.png', 44, 259.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1039001, '京瓷制造商', '严选想为你的厨房生活，带来新鲜感和活力，\n深入全国各地，选择日本京瓷等品牌代工厂，\n打造钻石系列厨具，颜值与品质兼具。', 'http://yanxuan.nosdn.127.net/3dda530605e3ab1c82d5ed30f2489473.png', 38, 89.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1040000, 'Tescom制造商', '严选为打造时尚健康的个护电器，\n选择Tescom品牌制造商，全球最大个护电器工厂之一，\n拥有20年经验，出口180多个国家，品质有保障。', 'http://yanxuan.nosdn.127.net/c17cd65971189fdc28f5bd6b78f657a7.png', 15, 59.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1041000, 'BCBG制造商', '严选从产品源头开始，每道工序质量把关，\n选择美国知名品牌BCBG的制造商合作，\n严谨匠心，致力于优质柔滑的睡衣穿搭产品。', 'http://yanxuan.nosdn.127.net/b9072023afd3621714fd5c49f140fca8.png', 36, 99.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);
INSERT INTO `base_brand` VALUES (1046000, 'Police制造商', '严选团队选定Police品牌制造商合作，\n有11年眼镜生产资质，兼顾品质与品味，\n为你带来专业时尚的墨镜。', 'http://yanxuan.nosdn.127.net/66e2cb956a9dd1efc7732bea278e901e.png', 6, 109.00, '2018-02-01 00:00:00', '2018-02-01 00:00:00', 0, NULL);

SET FOREIGN_KEY_CHECKS = 1;
