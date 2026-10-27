package com.pvptraining.bot;

/**
 * Perfis de bot. E apenas um conjunto de parametros lidos pelas tarefas de IA
 * (movimento, combate, cura). Nao ha "inteligencia" alem de regras + aleatoriedade.
 */
public enum BotProfile {
    //            nome            vel   alcPref alcance interv jitter acerto dano strafe flipMin flipMax sopas limiar reacao
    BEGINNER      ("Beginner",     0.80, 2.6,  2.8,  20, 15, 0.45, 3.0, false, 0,  0,  0, 0.0,  0),
    AGGRESSIVE    ("Aggressive",   1.15, 2.3,  3.0,  10,  4, 0.80, 5.0, false, 0,  0,  0, 0.0,  0),
    COMBO_MASTER  ("Combo Master", 1.05, 2.5,  3.0,   9,  3, 0.85, 5.0, true,  15, 40, 0, 0.0,  0),
    RECRAFT_MASTER("Recraft Master",1.00,2.5,  3.0,  11,  4, 0.75, 5.0, true,  20, 50, 16, 0.55, 8);

    public final String displayName;
    /** multiplicador de velocidade do navegador (1.0 = velocidade base do atributo). */
    public final double moveSpeed;
    /** distancia que o bot tenta manter do alvo. */
    public final double preferredRange;
    /** distancia maxima para tentar um golpe. */
    public final double reach;
    /** ticks minimos entre ataques (20 ticks = 1s). */
    public final int attackInterval;
    /** aleatoriedade somada ao intervalo. */
    public final int attackJitter;
    /** probabilidade de o golpe acertar (0..1), simula erro de mira/timing. */
    public final double hitChance;
    public final double damage;
    public final boolean strafes;
    public final int strafeFlipMin, strafeFlipMax;
    public final int soups;
    /** fracao da vida maxima abaixo da qual o bot toma sopa. */
    public final double soupThreshold;
    /** ticks de "reacao" antes de a sopa fazer efeito (o bot nao ataca nesse periodo). */
    public final int soupReaction;

    BotProfile(String displayName, double moveSpeed, double preferredRange, double reach, int attackInterval,
               int attackJitter, double hitChance, double damage, boolean strafes, int flipMin, int flipMax,
               int soups, double soupThreshold, int soupReaction) {
        this.displayName = displayName; this.moveSpeed = moveSpeed; this.preferredRange = preferredRange;
        this.reach = reach; this.attackInterval = attackInterval; this.attackJitter = attackJitter;
        this.hitChance = hitChance; this.damage = damage; this.strafes = strafes;
        this.strafeFlipMin = flipMin; this.strafeFlipMax = flipMax; this.soups = soups;
        this.soupThreshold = soupThreshold; this.soupReaction = soupReaction;
    }

    public static BotProfile fromName(String name) {
        for (BotProfile p : values()) {
            if (p.name().equalsIgnoreCase(name) || p.displayName.replace(" ", "").equalsIgnoreCase(name)) return p;
        }
        return null;
    }
}
