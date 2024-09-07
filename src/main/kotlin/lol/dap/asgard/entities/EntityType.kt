package lol.dap.asgard.entities

enum class EntityType(val id: Int) {

    ITEM(1),
    XP_ORB(2),
    LEASH_KNOT(8),
    PAINTING(9),
    ARROW(10),
    SNOWBALL(11),
    FIREBALL(12),
    SMALL_FIREBALL(13),
    ENDER_PEARL(14),
    EYE_OF_ENDER_SIGNAL(15),
    POTION(16),
    EXP_BOTTLE(17),
    ITEM_FRAME(18),
    WITHER_SKULL(19),
    PRIMED_TNT(20),
    FALLING_BLOCK(21),
    FIREWORK_ROCKET(22),
    ARMOR_STAND(30),
    MINECART_COMMAND_BLOCK(40),
    BOAT(41),
    MINECART_RIDEABLE(42),
    MINECART_CHEST(43),
    MINECART_FURNACE(44),
    MINECART_TNT(45),
    MINECART_HOPPER(46),
    MINECART_MOB_SPAWNER(47),
    CREEPER(50),
    SKELETON(51),
    SPIDER(52),
    GIANT(53),
    ZOMBIE(54),
    SLIME(55),
    GHAST(56),
    PIG_ZOMBIE(57),
    ENDERMAN(58),
    CAVE_SPIDER(59),
    SILVERFISH(60),
    BLAZE(61),
    MAGMA_CUBE(62),
    ENDER_DRAGON(63),
    WITHER(64),
    BAT(65),
    WITCH(66),
    ENDERMITE(67),
    GUARDIAN(68),
    PIG(90),
    SHEEP(91),
    COW(92),
    CHICKEN(93),
    SQUID(94),
    WOLF(95),
    MUSHROOM_COW(96),
    SNOWMAN(97),
    OCELOT(98),
    IRON_GOLEM(99),
    HORSE(100),
    RABBIT(101),
    VILLAGER(120),
    PLAYER(Int.MAX_VALUE);

    companion object {

        fun getById(id: Int): EntityType? {
            return EntityType.entries.find { it.id == id }
        }

    }

}
