# PvP Training 1.8.9 — Ultimate Soup Edition (WIP)

## Status honesto
**Nada aqui foi compilado nem executado.** O ambiente onde o código foi escrito não tinha `javac`, Gradle nem rede.
O código usa nomes da API Forge 1.8.9 / mapeamentos MCP `stable_22` escritos de memória; **espere corrigir alguns erros de compilação**.
Não existe JAR, não existe mapa, não existem `gradlew`/`gradle/wrapper` (o wrapper precisa ser gerado com rede).

## Implementado (código escrito, NÃO testado)
- Estrutura Forge 1.8.9 (ForgeGradle 2.1, Forge `1.8.9-11.15.1.2318-1.8.9`, JDK 8).
- Entidade `EntityTrainingBot` (humanoide, equipamento, vida, morte, NBT, não despawna).
- IA por regras, separada em tarefas: alvo (`BotTargetAI`), movimento (`BotMoveAI`: perseguição, strafe, recuo, detecção de "preso"), combate (`BotCombatAI`: alcance, linha de visão, recarga, chance de acerto) e cura (`tickHealing` na entidade: toma sopa abaixo de um limiar, com tempo de reação).
- Perfis: Beginner, Aggressive, Combo Master, Recraft Master (só parâmetros diferentes; veja limitações).
- Soup PvP para o jogador: clique direito com sopa de cogumelo cura 7 (3,5 corações) e vira pote (`SoupHandler`).
- Comandos: `/pvpt spawn [perfil]`, `/pvpt kit [sopas]`, `/pvpt clear`.

## NÃO implementado ainda
Aim, Combo, Recraft (salas/desafios/pontuação), Gladiator (arena, contagem, revanche), Trap, Movement,
perfis Gladiator / Trap Master / Ultimate, colocação de blocos por bots, menu GUI, estatísticas e persistência, mapa/gerador de mapa.

## Limitações conhecidas da IA
- É uma máquina de regras + aleatoriedade, não IA avançada. Sem W-tap/S-tap, sem knockback de sprint extra, sem uso de poções.
- Strafe e recuo aplicam velocidade direta (`motionX/Z`), não teclas simuladas.
- Dano é fixo por perfil (aplicado via `attackEntityFrom`), independente da espada na mão do bot.
- Alvos criativo/espectador são ignorados; teste em sobrevivência.
- `SoupHandler` depende de o Forge 1.8.9 disparar `PlayerInteractEvent` (RIGHT_CLICK_AIR) no servidor; **precisa ser verificado em jogo**. Se não curar, a alternativa é item customizado.

## Como compilar (na sua máquina, com internet)
1. Instale JDK 8 e Gradle 2.14 (ForgeGradle 2.1 não roda em Gradle/JDK modernos).
2. Na pasta do projeto: `gradle setupDecompWorkspace` e depois `gradle build`. JAR em `build/libs/`.
3. Teste: `gradle runClient`.
4. Se o repositório do ForgeGradle 2.1 falhar, é um problema conhecido da infraestrutura antiga; ajuste os `repositories` do `buildscript`.
5. Launcher oficial: instale Forge 1.8.9 (11.15.1.2318), coloque o JAR em `.minecraft/mods`, abra o perfil Forge.

## Teste mínimo (Fase 2/3)
Mundo individual, modo sobrevivência com cheats: `/pvpt kit`, `/pvpt spawn recraft_master`; ao ficar com vida baixa, clique direito com sopa.
