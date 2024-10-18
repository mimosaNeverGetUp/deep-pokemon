/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PokemonTranslationServiceTest {

    @Autowired
    private PokemonTranslationService pokemonTranslationService;

    @Test
    void getTranslation() {
        assertEquals("火焰之躯", pokemonTranslationService.getTranslation("Flame Body"));
        assertEquals("火焰鸟", pokemonTranslationService.getTranslation("Moltres"));
        assertEquals("吃剩的东西", pokemonTranslationService.getTranslation("Leftovers"));
        assertEquals("吃剩的东西", pokemonTranslationService.getTranslation("Leftovers"));
        assertEquals("黏美龙-洗翠", pokemonTranslationService.getTranslation("Goodra-Hisui"));
        assertEquals("黏美龙-洗翠", pokemonTranslationService.getTranslation("Goodra-H"));
        assertEquals("黏美龙-洗翠", pokemonTranslationService.getTranslation("Hisuian Goodra"));
        assertEquals("大剑鬼-洗翠", pokemonTranslationService.getTranslation("Hisuian Samurott"));
        assertEquals("风速狗-洗翠", pokemonTranslationService.getTranslation("Hisuian Arcanine"));
        assertEquals("火焰鸟-伽勒尔", pokemonTranslationService.getTranslation("Moltres-Galar"));
        assertEquals("火焰鸟-伽勒尔", pokemonTranslationService.getTranslation("Galarian Moltres"));
        assertEquals("火焰鸟-伽勒尔", pokemonTranslationService.getTranslation("Galarian Moltres"));
        assertEquals("九尾-阿罗拉", pokemonTranslationService.getTranslation("Alolan Ninetales"));
        assertEquals("九尾-阿罗拉", pokemonTranslationService.getTranslation("Ninetales-Alola"));
        assertEquals("巨钳螳螂-超级进化", pokemonTranslationService.getTranslation("Mega Scizor"));
        assertEquals("喷火龙-超级进化-X", pokemonTranslationService.getTranslation("Mega Charizard X"));
        assertEquals("吃剩的东西", pokemonTranslationService.getTranslation("Leftovers"));
        assertEquals("讲究眼镜猛雷鼓", pokemonTranslationService.getTranslation("Choice Specs Raging Bolt"));
        assertEquals("内敛 (+特攻, -攻击)猛雷鼓", pokemonTranslationService.getTranslation("Modest Raging Bolt"));
    }

    @Test
    void translateText() {
        String text = "Leveraging its passable bulk, amazing typing, solid movepool, " +
                "and two fantastic abilities in Magic Guard and Unaware, Clefable establishes its niche in the OU tier as a solid glue on bulkier team compositions, which appreciate its amazing role compression. Clefable maximizes its physical bulk to handle a myriad of physical attackers and passive Pokemon such as Great Tusk, Hisuian Samurott, Gliscor, and Garganacl. Clefable is an effective Stealth Rock user, as it threatens Great Tusk and can punish Corviknight with paralysis, lock it into Defog with Encore, or remove its item. Knock Off removes Heavy-Duty Boots to enable Clefable's Stealth Rock and teammates' Spikes. Clefable primarily uses Encore on Unaware sets, as it can force setup sweepers like Raging Bolt into uncomfortable positions, allowing one of its teammates to get a safe switch into the field. Thunder Wave cripples faster Pokemon like Zamazenta, Roaring Moon, and Weavile. While Moonlight is usually preferred as Clefable's recovery, Wish and Protect can be used over Moonlight and Stealth Rock, respectively, if Clefable's team already has another Stealth Rock user. Calm Mind can be used on Unaware sets to handle special-boosting attackers such as Darkrai and Hatterene. Sticky Barb + Trick can also be used on Magic Guard sets to incapacitate Gliscor, as Sticky Barb neutralizes Poison Heal recovery, crippling Gliscor's longevity; it also punishes common switch-ins like Galarian Slowking, Clodsire, and Blissey while also generally being useful to steal items. Clefable uses Tera Water and Tera Steel to absorb Steel-type attacks in a pinch like Kingambit's Iron Head, Gholdengo's Make It Rain, and Iron Treads's Steel Beam. Tera Water is preferred due to its good matchups into the physical attackers Clefable aims to check, letting Clefable switch into Barraskewda and Cinderace while granting a good neutral defensive typing. Tera Steel can also be used to become immune to Poison-type attacks like Galarian Slowking's Sludge Bomb, but it loses key matchups like Cinderace, Great Tusk, and Landorus-T. Tera Ghost is also a decent pick, as it preserves Clefable’s good matchups into Fighting-types while also letting it spinblock, but it has a worse matchup against Knock Off users. It's worth noting that Clefable prefers not to Terastallize unless it absolutely needs to, as its base typing is incredibly valuable. Rocky Helmet is also viable on Magic Guard sets as an effective way to punish U-turn and as a one time Knock Off punisher, although Clefable usually prefers Leftovers letting it beat stronger Pokemon one-on-one without needing to use Moonlight as frequently.</p> <p>Clefable fits on balance and stall teams that appreciate its role compression and typing. Teammates like Skarmory, Gliscor, and Clodsire love Clefable’s ability to remove Heavy-Duty Boots with Knock Off and enable their entry hazards. Gliscor, in particular, is a phenomenal teammate. The two are effective walls that cover each other’s physical weaknesses; Clefable can switch into Knock Off from threats that Gliscor cannot handle, namely Meowscarada, Great Tusk, Hisuian Samurott, and Weavile, threatening them out with Moonblast. In return, Gliscor can set Spikes, which when combined with Clefable’s Stealth Rock and the pair’s access to Knock Off, forms a potent hazard-stacking core. Galarian Slowking is similarly a good partner, as Clefable can handle many of the aforementioned Dark-types that threaten it. In return Galarian Slowking is an amazing special wall that switches into many strong special attacks that would threaten Clefable, such as Iron Moth's Sludge Wave and Primarina's Moonblast. It can also spread Toxic, which allows Clefable to stay in against physical attackers more comfortably. Encore’s ability to catch setup sweepers is appreciated by Clefable’s more offensively oriented teammates like Kingambit and Dragapult to give them a safe entry point. Additionally, it lets in more defensively oriented Pokemon, notably Alomomola, which can regenerate HP and pass Wish. Offensive partners appreciate Thunder Wave support to enable them to punish faster Pokemon like Iron Valiant, Iron Boulder, and opposing Dragapult. In exchange, Weavile and Meowscarada can remove Heavy-Duty Boots so Clefable's Stealth Rock can start racking up damage. Dragapult and Gholdengo are also effective partners, as they pressure or cripple common Pokemon that annoy Clefable and can also spinblock Great Tusk and Iron Treads. In return, Clefable threatens Dark-types while also forcing out Garganacl. Kingambit is a phenomenal partner, as it threatens common answers to Clefable such as Clodsire, Blissey, Galarian Slowking, and Gholdengo. Zamazenta pressures Iron Treads, Kingambit, and Heatran while equally enjoying Clefable handling Great Tusk and pestering Dragapult. Dragonite is also a decent partner, as it likes Clefable's ability to check and pressure Dragon-types as well as its Encore support, giving it more setup opportunities.";
        String translateText = pokemonTranslationService.translateText(text);
        assertNotEquals(text, translateText);
    }
}