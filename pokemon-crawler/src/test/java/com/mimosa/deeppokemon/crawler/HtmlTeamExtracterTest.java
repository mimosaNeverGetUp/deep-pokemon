package com.mimosa.deeppokemon.crawler;

import com.alibaba.fastjson.JSONObject;
import com.mimosa.deeppokemon.entity.Battle;
import javafx.util.Pair;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class HtmlTeamExtracterTest {
    @Autowired
    private HtmlTeamExtracter htmlTeamExtracter;

    @Test
    void extractHealthLineDataTest() throws Exception {
        String html = new String("\n" +
                "<!DOCTYPE html>\n" +
                "<html><head>\n" +
                "\n" +
                "\t<meta charset=\"utf-8\" />\n" +
                "\n" +
                "\t<title>[Gen 8] OU replay: Kyxor vs. Storm Zone - Pok&eacute;mon Showdown</title>\n" +
                "\n" +
                "\t<meta name=\"description\" content=\"Watch a replay of a Pokémon battle between Kyxor and Storm Zone ([Gen 8] OU)\" />\n" +
                "\n" +
                "\t<meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge,chrome=IE8\" />\n" +
                "\t<link rel=\"stylesheet\" href=\"//play.pokemonshowdown.com/style/font-awesome.css?932f42c7\" />\n" +
                "\t<link rel=\"stylesheet\" href=\"//pokemonshowdown.com/theme/panels.css?0.19894602666775563\" />\n" +
                "\t<link rel=\"stylesheet\" href=\"//pokemonshowdown.com/theme/main.css?0.13800795205058014\" />\n" +
                "\t<link rel=\"stylesheet\" href=\"//play.pokemonshowdown.com/style/battle.css?93ec63dc\" />\n" +
                "\t<link rel=\"stylesheet\" href=\"//play.pokemonshowdown.com/style/replay.css?cfa51183\" />\n" +
                "\t<link rel=\"stylesheet\" href=\"//play.pokemonshowdown.com/style/utilichart.css?e39c48cf\" />\n" +
                "\n" +
                "\t<!-- Workarounds for IE bugs to display trees correctly. -->\n" +
                "\t<!--[if lte IE 6]><style> li.tree { height: 1px; } </style><![endif]-->\n" +
                "\t<!--[if IE 7]><style> li.tree { zoom: 1; } </style><![endif]-->\n" +
                "\n" +
                "\t<script type=\"text/javascript\">\n" +
                "\t\tvar _gaq = _gaq || [];\n" +
                "\t\t_gaq.push(['_setAccount', 'UA-26211653-1']);\n" +
                "\t\t_gaq.push(['_setDomainName', 'pokemonshowdown.com']);\n" +
                "\t\t_gaq.push(['_setAllowLinker', true]);\n" +
                "\t\t_gaq.push(['_trackPageview']);\n" +
                "\n" +
                "\t\t(function() {\n" +
                "\t\t\tvar ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n" +
                "\t\t\tga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n" +
                "\t\t\tvar s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n" +
                "\t\t})();\n" +
                "\t</script>\n" +
                "</head><body>\n" +
                "\n" +
                "\t<div class=\"pfx-topbar\">\n" +
                "\t\t<div class=\"header\">\n" +
                "\t\t\t<ul class=\"nav\">\n" +
                "\t\t\t\t<li><a class=\"button nav-first\" href=\"//pokemonshowdown.com/?0.02070551938312626\"><img src=\"//pokemonshowdown.com/images/pokemonshowdownbeta.png?0.8309447989374927\" alt=\"Pok&eacute;mon Showdown! (beta)\" /> Home</a></li>\n" +
                "\t\t\t\t<li><a class=\"button\" href=\"//dex.pokemonshowdown.com/?0.7905013690071625\">Pok&eacute;dex</a></li>\n" +
                "\t\t\t\t<li><a class=\"button cur\" href=\"/?0.3182601107399814\">Replays</a></li>\n" +
                "\t\t\t\t<li><a class=\"button\" href=\"//pokemonshowdown.com/ladder/?0.8785929064252531\">Ladder</a></li>\n" +
                "\t\t\t\t<li><a class=\"button nav-last\" href=\"//pokemonshowdown.com/forums/?0.5481319715039552\">Forum</a></li>\n" +
                "\t\t\t</ul>\n" +
                "\t\t\t<ul class=\"nav nav-play\">\n" +
                "\t\t\t\t<li><a class=\"button greenbutton nav-first nav-last\" href=\"http://play.pokemonshowdown.com/\">Play</a></li>\n" +
                "\t\t\t</ul>\n" +
                "\t\t\t<div style=\"clear:both\"></div>\n" +
                "\t\t</div>\n" +
                "\t</div>\n" +
                "\t<div class=\"pfx-panel\"><div class=\"pfx-body\" style=\"max-width:1180px\">\n" +
                "\t\t<div class=\"wrapper replay-wrapper\">\n" +
                "\n" +
                "\t\t\t<div class=\"battle\"><div class=\"playbutton\"><button disabled>Loading...</button></div></div>\n" +
                "\t\t\t<div class=\"battle-log\"></div>\n" +
                "\t\t\t<div class=\"replay-controls\">\n" +
                "\t\t\t\t<button data-action=\"start\"><i class=\"fa fa-play\"></i> Play</button>\n" +
                "\t\t\t</div>\n" +
                "\t\t\t<div class=\"replay-controls-2\">\n" +
                "\t\t\t\t<div class=\"chooser leftchooser speedchooser\">\n" +
                "\t\t\t\t\t<em>Speed:</em>\n" +
                "\t\t\t\t\t<div><button value=\"hyperfast\">Hyperfast</button> <button value=\"fast\">Fast</button><button value=\"normal\" class=\"sel\">Normal</button><button value=\"slow\">Slow</button><button value=\"reallyslow\">Really Slow</button></div>\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t\t<div class=\"chooser colorchooser\">\n" +
                "\t\t\t\t\t<em>Color&nbsp;scheme:</em>\n" +
                "\t\t\t\t\t<div><button class=\"sel\" value=\"light\">Light</button><button value=\"dark\">Dark</button></div>\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t\t<div class=\"chooser soundchooser\" style=\"display:none\">\n" +
                "\t\t\t\t\t<em>Music:</em>\n" +
                "\t\t\t\t\t<div><button class=\"sel\" value=\"on\">On</button><button value=\"off\">Off</button></div>\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t</div>\n" +
                "\t\t\t<!--[if lte IE 8]>\n" +
                "\t\t\t\t<div class=\"error\"><p>&#3232;_&#3232; <strong>You're using an old version of Internet Explorer.</strong></p>\n" +
                "\t\t\t\t<p>We use some transparent backgrounds, rounded corners, and other effects that your old version of IE doesn't support.</p>\n" +
                "\t\t\t\t<p>Please install <em>one</em> of these: <a href=\"http://www.google.com/chrome\">Chrome</a> | <a href=\"http://www.mozilla.org/en-US/firefox/\">Firefox</a> | <a href=\"http://windows.microsoft.com/en-US/internet-explorer/products/ie/home\">Internet Explorer 9</a></p></div>\n" +
                "\t\t\t<![endif]-->\n" +
                "\n" +
                "\t\t\t\n" +
                "\t\t\t<pre class=\"urlbox\" style=\"word-wrap: break-word;\">https://replay.pokemonshowdown.com/gen8ou-1153524552</pre>\n" +
                "\n" +
                "\t\t\t<h1 style=\"font-weight:normal;text-align:left\"><strong>[Gen 8] OU</strong>: <a href=\"//replay.pokemonshowdown.com/kyxor\" class=\"subtle\">Kyxor</a> vs. <a href=\"//replay.pokemonshowdown.com/stormzone\" class=\"subtle\">Storm Zone</a></h1>\n" +
                "\t\t\t<p style=\"padding:0 1em;margin-top:0\">\n" +
                "\t\t\t\t<small class=\"uploaddate\" data-timestamp=\"1595099780\"><em>Uploaded:</em> Jul 18, 2020</small>\n" +
                "\t\t\t</p>\n" +
                "\n" +
                "\t\t\t<div id=\"loopcount\"></div>\n" +
                "\t\t</div>\n" +
                "\n" +
                "\t\t<input type=\"hidden\" name=\"replayid\" value=\"gen8ou-1153524552\" />\n" +
                "\t\t<!--\n" +
                "\n" +
                "You can get this log directly at https://replay.pokemonshowdown.com/gen8ou-1153524552.log\n" +
                "\n" +
                "Or with metadata at https://replay.pokemonshowdown.com/gen8ou-1153524552.json\n" +
                "\n" +
                "Most PS pages you'd want to scrape will have a .json version!\n" +
                "\n" +
                "\t\t-->\n" +
                "\t\t<script type=\"text/plain\" class=\"log\">|j|☆Kyxor\n" +
                "|j|☆Storm Zone\n" +
                "|inactive|Battle timer is ON: inactive players will automatically lose when time's up.\n" +
                "|player|p1|Kyxor|blackbelt|\n" +
                "|player|p2|Storm Zone|lusamine-nihilego|\n" +
                "|teamsize|p1|6\n" +
                "|teamsize|p2|6\n" +
                "|gametype|singles\n" +
                "|gen|8\n" +
                "|tier|[Gen 8] OU\n" +
                "|rated|Tournament battle\n" +
                "|clearpoke\n" +
                "|poke|p1|Ferrothorn, F|\n" +
                "|poke|p1|Rillaboom, F|\n" +
                "|poke|p1|Corviknight, F|\n" +
                "|poke|p1|Cinderace, M|\n" +
                "|poke|p1|Kommo-o, M|\n" +
                "|poke|p1|Zeraora|\n" +
                "|poke|p2|Haxorus, M|\n" +
                "|poke|p2|Primarina, M|\n" +
                "|poke|p2|Ferrothorn, M|\n" +
                "|poke|p2|Volcarona, M|\n" +
                "|poke|p2|Diggersby, M|\n" +
                "|poke|p2|Hydreigon, M|\n" +
                "|rule|Sleep Clause Mod: Limit one foe put to sleep\n" +
                "|rule|Species Clause: Limit one of each Pokémon\n" +
                "|rule|OHKO Clause: OHKO moves are banned\n" +
                "|rule|Evasion Moves Clause: Evasion moves are banned\n" +
                "|rule|Endless Battle Clause: Forcing endless battles is banned\n" +
                "|rule|HP Percentage Mod: HP is shown in percentages\n" +
                "|rule|Dynamax Clause: You cannot dynamax\n" +
                "|teampreview\n" +
                "|j| hipster garbage\n" +
                "|j| Dread Arceus\n" +
                "|inactive|Kyxor has 120 seconds left.\n" +
                "|\n" +
                "|start\n" +
                "|switch|p1a: Rillaboom|Rillaboom, F|100\\/100\n" +
                "|switch|p2a: Predator|Haxorus, M|100\\/100\n" +
                "|-ability|p2a: Predator|Mold Breaker\n" +
                "|-fieldstart|move: Grassy Terrain|[from] ability: Grassy Surge|[of] p1a: Rillaboom\n" +
                "|-enditem|p1a: Rillaboom|Grassy Seed\n" +
                "|-boost|p1a: Rillaboom|def|1|[from] item: Grassy Seed\n" +
                "|turn|1\n" +
                "|j|%Primal Unova\n" +
                "|j| I Eat Snoms\n" +
                "|\n" +
                "|move|p1a: Rillaboom|Fake Out|p2a: Predator\n" +
                "|-damage|p2a: Predator|84\\/100\n" +
                "|cant|p2a: Predator|flinch\n" +
                "|\n" +
                "|-heal|p2a: Predator|90\\/100|[from] Grassy Terrain\n" +
                "|upkeep\n" +
                "|turn|2\n" +
                "|j| Larmes D'Ange\n" +
                "|\n" +
                "|switch|p2a: Hybrid|Hydreigon, M|100\\/100\n" +
                "|move|p1a: Rillaboom|High Horsepower|p2a: Hybrid\n" +
                "|-immune|p2a: Hybrid|[from] ability: Levitate\n" +
                "|\n" +
                "|upkeep\n" +
                "|turn|3\n" +
                "|j|+Skypenguin\n" +
                "|\n" +
                "|switch|p1a: Ferrothorn|Ferrothorn, F|100\\/100\n" +
                "|move|p2a: Hybrid|Flamethrower|p1a: Ferrothorn\n" +
                "|-supereffective|p1a: Ferrothorn\n" +
                "|-damage|p1a: Ferrothorn|0 fnt\n" +
                "|faint|p1a: Ferrothorn\n" +
                "|\n" +
                "|upkeep\n" +
                "|\n" +
                "|switch|p1a: Cinderace|Cinderace, M|100\\/100\n" +
                "|turn|4\n" +
                "|\n" +
                "|switch|p2a: Happiness|Primarina, M|100\\/100\n" +
                "|move|p1a: Cinderace|High Jump Kick|p2a: Happiness\n" +
                "|-start|p1a: Cinderace|typechange|Fighting|[from] ability: Libero\n" +
                "|-resisted|p2a: Happiness\n" +
                "|-damage|p2a: Happiness|60\\/100\n" +
                "|\n" +
                "|-heal|p2a: Happiness|66\\/100|[from] item: Leftovers\n" +
                "|-heal|p2a: Happiness|72\\/100|[from] Grassy Terrain\n" +
                "|upkeep\n" +
                "|turn|5\n" +
                "|\n" +
                "|switch|p1a: Rillaboom|Rillaboom, F|100\\/100\n" +
                "|move|p2a: Happiness|Draining Kiss|p1a: Rillaboom\n" +
                "|-damage|p1a: Rillaboom|74\\/100\n" +
                "|-heal|p2a: Happiness|91\\/100|[from] drain|[of] p1a: Rillaboom\n" +
                "|\n" +
                "|-heal|p1a: Rillaboom|80\\/100|[from] Grassy Terrain\n" +
                "|-heal|p2a: Happiness|97\\/100|[from] item: Leftovers\n" +
                "|-heal|p2a: Happiness|100\\/100|[from] Grassy Terrain\n" +
                "|-fieldend|move: Grassy Terrain\n" +
                "|upkeep\n" +
                "|turn|6\n" +
                "|j| venson04\n" +
                "|\n" +
                "|switch|p2a: Temple|Ferrothorn, M|100\\/100\n" +
                "|move|p1a: Rillaboom|Fake Out|p2a: Temple\n" +
                "|-resisted|p2a: Temple\n" +
                "|-damage|p2a: Temple|96\\/100\n" +
                "|-damage|p1a: Rillaboom|68\\/100|[from] ability: Iron Barbs|[of] p2a: Temple\n" +
                "|\n" +
                "|-heal|p2a: Temple|100\\/100|[from] item: Leftovers\n" +
                "|upkeep\n" +
                "|turn|7\n" +
                "|\n" +
                "|move|p1a: Rillaboom|High Horsepower|p2a: Temple\n" +
                "|-damage|p2a: Temple|77\\/100\n" +
                "|-damage|p1a: Rillaboom|55\\/100|[from] ability: Iron Barbs|[of] p2a: Temple\n" +
                "|move|p2a: Temple|Knock Off|p1a: Rillaboom\n" +
                "|-damage|p1a: Rillaboom|40\\/100\n" +
                "|\n" +
                "|-heal|p2a: Temple|83\\/100|[from] item: Leftovers\n" +
                "|upkeep\n" +
                "|turn|8\n" +
                "|j| HYper\n" +
                "|c|%Primal Unova|yknow, you can always just give him a chance Storm Zone\n" +
                "|c|☆Kyxor|why did i just have a deja vu-\n" +
                "|c| HYper|Kyxor shouldve brought band mold breaker drill smh\n" +
                "|c|☆Storm Zone|he has one tho\n" +
                "|inactive|Kyxor has 120 seconds left.\n" +
                "|\n" +
                "|move|p1a: Rillaboom|High Horsepower|p2a: Temple|[miss]\n" +
                "|-miss|p1a: Rillaboom|p2a: Temple\n" +
                "|move|p2a: Temple|Knock Off|p1a: Rillaboom\n" +
                "|-damage|p1a: Rillaboom|25\\/100\n" +
                "|\n" +
                "|-heal|p2a: Temple|90\\/100|[from] item: Leftovers\n" +
                "|upkeep\n" +
                "|turn|9\n" +
                "|\n" +
                "|move|p1a: Rillaboom|High Horsepower|p2a: Temple|[miss]\n" +
                "|-miss|p1a: Rillaboom|p2a: Temple\n" +
                "|move|p2a: Temple|Stealth Rock|p1a: Rillaboom\n" +
                "|-sidestart|p1: Kyxor|move: Stealth Rock\n" +
                "|\n" +
                "|-heal|p2a: Temple|96\\/100|[from] item: Leftovers\n" +
                "|upkeep\n" +
                "|turn|10\n" +
                "|\n" +
                "|move|p1a: Rillaboom|High Horsepower|p2a: Temple\n" +
                "|-damage|p2a: Temple|74\\/100\n" +
                "|-damage|p1a: Rillaboom|13\\/100|[from] ability: Iron Barbs|[of] p2a: Temple\n" +
                "|move|p2a: Temple|Stealth Rock||[still]\n" +
                "|-fail|p2a: Temple\n" +
                "|\n" +
                "|-heal|p2a: Temple|80\\/100|[from] item: Leftovers\n" +
                "|upkeep\n" +
                "|turn|11\n" +
                "|c|%Primal Unova|you sure\n" +
                "|\n" +
                "|move|p1a: Rillaboom|High Horsepower|p2a: Temple\n" +
                "|-damage|p2a: Temple|56\\/100\n" +
                "|-damage|p1a: Rillaboom|0 fnt|[from] ability: Iron Barbs|[of] p2a: Temple\n" +
                "|faint|p1a: Rillaboom\n" +
                "|move|p2a: Temple|Stealth Rock||[still]\n" +
                "|-fail|p2a: Temple\n" +
                "|\n" +
                "|-heal|p2a: Temple|63\\/100|[from] item: Leftovers\n" +
                "|upkeep\n" +
                "|j| Bike Mike\n" +
                "|c|☆Storm Zone|fine ill let him chip ferro\n" +
                "|c|%Primal Unova|doesn't look like it\n" +
                "|\n" +
                "|switch|p1a: Cinderace|Cinderace, M|100\\/100\n" +
                "|-damage|p1a: Cinderace|76\\/100|[from] Stealth Rock\n" +
                "|turn|12\n" +
                "|j|%gomikyu\n" +
                "|\n" +
                "|move|p1a: Cinderace|Pyro Ball|p2a: Temple\n" +
                "|-supereffective|p2a: Temple\n" +
                "|-damage|p2a: Temple|0 fnt\n" +
                "|faint|p2a: Temple\n" +
                "|\n" +
                "|upkeep\n" +
                "|\n" +
                "|switch|p2a: Happiness|Primarina, M|100\\/100\n" +
                "|turn|13\n" +
                "|\n" +
                "|switch|p1a: Corviknight|Corviknight, F|100\\/100\n" +
                "|-damage|p1a: Corviknight|88\\/100|[from] Stealth Rock\n" +
                "|-ability|p1a: Corviknight|Pressure\n" +
                "|move|p2a: Happiness|Scald|p1a: Corviknight\n" +
                "|-damage|p1a: Corviknight|56\\/100\n" +
                "|-status|p1a: Corviknight|brn\n" +
                "|\n" +
                "|-damage|p1a: Corviknight|50\\/100 brn|[from] brn\n" +
                "|upkeep\n" +
                "|turn|14\n" +
                "|\n" +
                "|move|p1a: Corviknight|Tailwind|p1a: Corviknight\n" +
                "|-sidestart|p1: Kyxor|move: Tailwind\n" +
                "|move|p2a: Happiness|Substitute|p2a: Happiness\n" +
                "|-start|p2a: Happiness|Substitute\n" +
                "|-damage|p2a: Happiness|75\\/100\n" +
                "|\n" +
                "|-heal|p2a: Happiness|82\\/100|[from] item: Leftovers\n" +
                "|-damage|p1a: Corviknight|44\\/100 brn|[from] brn\n" +
                "|upkeep\n" +
                "|turn|15\n" +
                "|l| hipster garbage\n" +
                "|\n" +
                "|move|p1a: Corviknight|Iron Head|p2a: Happiness\n" +
                "|-activate|p2a: Happiness|move: Substitute|[damage]\n" +
                "|move|p2a: Happiness|Calm Mind|p2a: Happiness\n" +
                "|-boost|p2a: Happiness|spa|1\n" +
                "|-boost|p2a: Happiness|spd|1\n" +
                "|\n" +
                "|-heal|p2a: Happiness|88\\/100|[from] item: Leftovers\n" +
                "|-damage|p1a: Corviknight|38\\/100 brn|[from] brn\n" +
                "|upkeep\n" +
                "|turn|16\n" +
                "|\n" +
                "|move|p1a: Corviknight|Iron Head|p2a: Happiness\n" +
                "|-end|p2a: Happiness|Substitute\n" +
                "|move|p2a: Happiness|Calm Mind|p2a: Happiness\n" +
                "|-boost|p2a: Happiness|spa|1\n" +
                "|-boost|p2a: Happiness|spd|1\n" +
                "|\n" +
                "|-heal|p2a: Happiness|94\\/100|[from] item: Leftovers\n" +
                "|-damage|p1a: Corviknight|32\\/100 brn|[from] brn\n" +
                "|upkeep\n" +
                "|turn|17\n" +
                "|\n" +
                "|move|p1a: Corviknight|Brave Bird|p2a: Happiness\n" +
                "|-damage|p2a: Happiness|73\\/100\n" +
                "|-damage|p1a: Corviknight|26\\/100 brn|[from] Recoil\n" +
                "|move|p2a: Happiness|Substitute|p2a: Happiness\n" +
                "|-start|p2a: Happiness|Substitute\n" +
                "|-damage|p2a: Happiness|48\\/100\n" +
                "|\n" +
                "|-heal|p2a: Happiness|54\\/100|[from] item: Leftovers\n" +
                "|-damage|p1a: Corviknight|20\\/100 brn|[from] brn\n" +
                "|-sideend|p1: Kyxor|move: Tailwind\n" +
                "|upkeep\n" +
                "|turn|18\n" +
                "|\n" +
                "|move|p1a: Corviknight|Brave Bird|p2a: Happiness\n" +
                "|-activate|p2a: Happiness|move: Substitute|[damage]\n" +
                "|-damage|p1a: Corviknight|14\\/100 brn|[from] Recoil|[of] p2a: Happiness\n" +
                "|move|p2a: Happiness|Draining Kiss|p1a: Corviknight\n" +
                "|-resisted|p1a: Corviknight\n" +
                "|-damage|p1a: Corviknight|0 fnt\n" +
                "|-heal|p2a: Happiness|65\\/100|[from] drain|[of] p1a: Corviknight\n" +
                "|-damage|p2a: Happiness|48\\/100|[from] item: Rocky Helmet|[of] p1a: Corviknight\n" +
                "|faint|p1a: Corviknight\n" +
                "|\n" +
                "|-heal|p2a: Happiness|54\\/100|[from] item: Leftovers\n" +
                "|upkeep\n" +
                "|\n" +
                "|switch|p1a: Zeraora|Zeraora|100\\/100\n" +
                "|-damage|p1a: Zeraora|88\\/100|[from] Stealth Rock\n" +
                "|turn|19\n" +
                "|\n" +
                "|move|p1a: Zeraora|Plasma Fists|p2a: Happiness\n" +
                "|-supereffective|p2a: Happiness\n" +
                "|-end|p2a: Happiness|Substitute\n" +
                "|-heal|p1a: Zeraora|89\\/100|[from] item: Shell Bell|[of] p2a: Happiness\n" +
                "|move|p2a: Happiness|Draining Kiss|p1a: Zeraora\n" +
                "|-damage|p1a: Zeraora|36\\/100\n" +
                "|-heal|p2a: Happiness|89\\/100|[from] drain|[of] p1a: Zeraora\n" +
                "|\n" +
                "|-heal|p2a: Happiness|95\\/100|[from] item: Leftovers\n" +
                "|upkeep\n" +
                "|turn|20\n" +
                "|\n" +
                "|move|p1a: Zeraora|Plasma Fists|p2a: Happiness\n" +
                "|-supereffective|p2a: Happiness\n" +
                "|-damage|p2a: Happiness|20\\/100\n" +
                "|-fieldactivate|move: Ion Deluge\n" +
                "|-heal|p1a: Zeraora|47\\/100|[from] item: Shell Bell|[of] p2a: Happiness\n" +
                "|move|p2a: Happiness|Draining Kiss|p1a: Zeraora\n" +
                "|-damage|p1a: Zeraora|0 fnt\n" +
                "|-heal|p2a: Happiness|50\\/100|[from] drain|[of] p1a: Zeraora\n" +
                "|faint|p1a: Zeraora\n" +
                "|\n" +
                "|-heal|p2a: Happiness|57\\/100|[from] item: Leftovers\n" +
                "|upkeep\n" +
                "|l| Bike Mike\n" +
                "|\n" +
                "|switch|p1a: Cinderace|Cinderace, M|76\\/100\n" +
                "|-damage|p1a: Cinderace|51\\/100|[from] Stealth Rock\n" +
                "|turn|21\n" +
                "|\n" +
                "|move|p1a: Cinderace|Gunk Shot|p2a: Happiness\n" +
                "|-start|p1a: Cinderace|typechange|Poison|[from] ability: Libero\n" +
                "|-supereffective|p2a: Happiness\n" +
                "|-damage|p2a: Happiness|0 fnt\n" +
                "|faint|p2a: Happiness\n" +
                "|\n" +
                "|upkeep\n" +
                "|\n" +
                "|switch|p2a: Lumberjack|Diggersby, M|100\\/100\n" +
                "|turn|22\n" +
                "|\n" +
                "|switch|p1a: Kommo-o|Kommo-o, M|100\\/100\n" +
                "|-damage|p1a: Kommo-o|94\\/100|[from] Stealth Rock\n" +
                "|move|p2a: Lumberjack|Swords Dance|p2a: Lumberjack\n" +
                "|-boost|p2a: Lumberjack|atk|2\n" +
                "|\n" +
                "|upkeep\n" +
                "|turn|23\n" +
                "|\n" +
                "|move|p1a: Kommo-o|Clangorous Soul|p1a: Kommo-o\n" +
                "|-boost|p1a: Kommo-o|atk|1\n" +
                "|-boost|p1a: Kommo-o|def|1\n" +
                "|-boost|p1a: Kommo-o|spa|1\n" +
                "|-boost|p1a: Kommo-o|spd|1\n" +
                "|-boost|p1a: Kommo-o|spe|1\n" +
                "|-damage|p1a: Kommo-o|61\\/100\n" +
                "|move|p2a: Lumberjack|Earthquake|p1a: Kommo-o\n" +
                "|-crit|p1a: Kommo-o\n" +
                "|-damage|p1a: Kommo-o|0 fnt\n" +
                "|-damage|p2a: Lumberjack|91\\/100|[from] item: Life Orb\n" +
                "|faint|p1a: Kommo-o\n" +
                "|\n" +
                "|upkeep\n" +
                "|\n" +
                "|switch|p1a: Cinderace|Cinderace, M|51\\/100\n" +
                "|-damage|p1a: Cinderace|26\\/100|[from] Stealth Rock\n" +
                "|turn|24\n" +
                "|\n" +
                "|move|p2a: Lumberjack|Quick Attack|p1a: Cinderace\n" +
                "|-damage|p1a: Cinderace|0 fnt\n" +
                "|-damage|p2a: Lumberjack|81\\/100|[from] item: Life Orb\n" +
                "|faint|p1a: Cinderace\n" +
                "|\n" +
                "|win|Storm Zone\n" +
                "|l|☆Storm Zone\n" +
                "|c|☆Kyxor|gg\n" +
                "|l| Larmes D'Ange\n" +
                "|l|+Primal Unova\n" +
                "|l| HYper\n" +
                "|l| Skypenguin\n" +
                "|l| gomikyu\n" +
                "</script>\n" +
                "\n" +
                "<div><script type=\"text/javascript\"><!--\n" +
                "google_ad_client = \"ca-pub-6535472412829264\";\n" +
                "/* PS replay */\n" +
                "google_ad_slot = \"6865298132\";\n" +
                "google_ad_width = 728;\n" +
                "google_ad_height = 90;\n" +
                "//-->\n" +
                "</script>\n" +
                "<script type=\"text/javascript\"\n" +
                "src=\"//pagead2.googlesyndication.com/pagead/show_ads.js\">\n" +
                "</script></div>\n" +
                "\n" +
                "\t\t<a href=\"/\" class=\"pfx-backbutton\" data-target=\"back\"><i class=\"fa fa-chevron-left\"></i> More replays</a>\n" +
                "\n" +
                "\t</div></div>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/js/lib/jquery-1.11.0.min.js?8fc25e27\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/js/lib/lodash.core.js?e9be4c2d\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/js/lib/backbone.js?8a8d8296\"></script>\n" +
                "\t<script src=\"//dex.pokemonshowdown.com/js/panels.js?0.31251708905664044\"></script>\n" +
                "\n" +
                "\t<script src=\"//play.pokemonshowdown.com/js/lib/jquery-cookie.js?38477214\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/js/lib/html-sanitizer-minified.js?949c4200\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/js/battle-sound.js?4dcdc9b5\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/config/config.js?6f55c5f6\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/js/battledata.js?ec203117\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/data/pokedex-mini.js?f9992121\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/data/pokedex-mini-bw.js?4c42d9f1\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/data/graphics.js?9cb31a46\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/data/pokedex.js?34f10d54\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/data/items.js?2b217bae\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/data/moves.js?64e80f65\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/data/abilities.js?6f27f7be\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/data/teambuilder-tables.js?b700bd1a\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/js/battle-tooltips.js?2b69e503\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/js/battle.js?7324c8f4\"></script>\n" +
                "\t<script src=\"/js/replay.js?6887ea68\"></script>\n" +
                "\n" +
                "</body></html>\n");
        Battle battle = htmlTeamExtracter.extract(html);

//        for (ArrayList<HashMap<String, Float>> arrayList : lists) {
//            for (HashMap<String, Float> hashMap : arrayList) {
//                System.out.println("______________________________________________________");
//                for (Map.Entry<String, Float> stringFloatEntry : hashMap.entrySet()) {
//                    System.out.println(stringFloatEntry.getKey() + stringFloatEntry.getValue());
//
//                }
//            }
//
//        }
    }

    @Test
    void extractHighLight() {
        String html = new String("\n" +
                "<!DOCTYPE html>\n" +
                "<html><head>\n" +
                "\n" +
                "\t<meta charset=\"utf-8\" />\n" +
                "\n" +
                "\t<title>[Gen 8] OU replay: Kyxor vs. Storm Zone - Pok&eacute;mon Showdown</title>\n" +
                "\n" +
                "\t<meta name=\"description\" content=\"Watch a replay of a Pokémon battle between Kyxor and Storm Zone ([Gen 8] OU)\" />\n" +
                "\n" +
                "\t<meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge,chrome=IE8\" />\n" +
                "\t<link rel=\"stylesheet\" href=\"//play.pokemonshowdown.com/style/font-awesome.css?932f42c7\" />\n" +
                "\t<link rel=\"stylesheet\" href=\"//pokemonshowdown.com/theme/panels.css?0.19894602666775563\" />\n" +
                "\t<link rel=\"stylesheet\" href=\"//pokemonshowdown.com/theme/main.css?0.13800795205058014\" />\n" +
                "\t<link rel=\"stylesheet\" href=\"//play.pokemonshowdown.com/style/battle.css?93ec63dc\" />\n" +
                "\t<link rel=\"stylesheet\" href=\"//play.pokemonshowdown.com/style/replay.css?cfa51183\" />\n" +
                "\t<link rel=\"stylesheet\" href=\"//play.pokemonshowdown.com/style/utilichart.css?e39c48cf\" />\n" +
                "\n" +
                "\t<!-- Workarounds for IE bugs to display trees correctly. -->\n" +
                "\t<!--[if lte IE 6]><style> li.tree { height: 1px; } </style><![endif]-->\n" +
                "\t<!--[if IE 7]><style> li.tree { zoom: 1; } </style><![endif]-->\n" +
                "\n" +
                "\t<script type=\"text/javascript\">\n" +
                "\t\tvar _gaq = _gaq || [];\n" +
                "\t\t_gaq.push(['_setAccount', 'UA-26211653-1']);\n" +
                "\t\t_gaq.push(['_setDomainName', 'pokemonshowdown.com']);\n" +
                "\t\t_gaq.push(['_setAllowLinker', true]);\n" +
                "\t\t_gaq.push(['_trackPageview']);\n" +
                "\n" +
                "\t\t(function() {\n" +
                "\t\t\tvar ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n" +
                "\t\t\tga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n" +
                "\t\t\tvar s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n" +
                "\t\t})();\n" +
                "\t</script>\n" +
                "</head><body>\n" +
                "\n" +
                "\t<div class=\"pfx-topbar\">\n" +
                "\t\t<div class=\"header\">\n" +
                "\t\t\t<ul class=\"nav\">\n" +
                "\t\t\t\t<li><a class=\"button nav-first\" href=\"//pokemonshowdown.com/?0.02070551938312626\"><img src=\"//pokemonshowdown.com/images/pokemonshowdownbeta.png?0.8309447989374927\" alt=\"Pok&eacute;mon Showdown! (beta)\" /> Home</a></li>\n" +
                "\t\t\t\t<li><a class=\"button\" href=\"//dex.pokemonshowdown.com/?0.7905013690071625\">Pok&eacute;dex</a></li>\n" +
                "\t\t\t\t<li><a class=\"button cur\" href=\"/?0.3182601107399814\">Replays</a></li>\n" +
                "\t\t\t\t<li><a class=\"button\" href=\"//pokemonshowdown.com/ladder/?0.8785929064252531\">Ladder</a></li>\n" +
                "\t\t\t\t<li><a class=\"button nav-last\" href=\"//pokemonshowdown.com/forums/?0.5481319715039552\">Forum</a></li>\n" +
                "\t\t\t</ul>\n" +
                "\t\t\t<ul class=\"nav nav-play\">\n" +
                "\t\t\t\t<li><a class=\"button greenbutton nav-first nav-last\" href=\"http://play.pokemonshowdown.com/\">Play</a></li>\n" +
                "\t\t\t</ul>\n" +
                "\t\t\t<div style=\"clear:both\"></div>\n" +
                "\t\t</div>\n" +
                "\t</div>\n" +
                "\t<div class=\"pfx-panel\"><div class=\"pfx-body\" style=\"max-width:1180px\">\n" +
                "\t\t<div class=\"wrapper replay-wrapper\">\n" +
                "\n" +
                "\t\t\t<div class=\"battle\"><div class=\"playbutton\"><button disabled>Loading...</button></div></div>\n" +
                "\t\t\t<div class=\"battle-log\"></div>\n" +
                "\t\t\t<div class=\"replay-controls\">\n" +
                "\t\t\t\t<button data-action=\"start\"><i class=\"fa fa-play\"></i> Play</button>\n" +
                "\t\t\t</div>\n" +
                "\t\t\t<div class=\"replay-controls-2\">\n" +
                "\t\t\t\t<div class=\"chooser leftchooser speedchooser\">\n" +
                "\t\t\t\t\t<em>Speed:</em>\n" +
                "\t\t\t\t\t<div><button value=\"hyperfast\">Hyperfast</button> <button value=\"fast\">Fast</button><button value=\"normal\" class=\"sel\">Normal</button><button value=\"slow\">Slow</button><button value=\"reallyslow\">Really Slow</button></div>\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t\t<div class=\"chooser colorchooser\">\n" +
                "\t\t\t\t\t<em>Color&nbsp;scheme:</em>\n" +
                "\t\t\t\t\t<div><button class=\"sel\" value=\"light\">Light</button><button value=\"dark\">Dark</button></div>\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t\t<div class=\"chooser soundchooser\" style=\"display:none\">\n" +
                "\t\t\t\t\t<em>Music:</em>\n" +
                "\t\t\t\t\t<div><button class=\"sel\" value=\"on\">On</button><button value=\"off\">Off</button></div>\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t</div>\n" +
                "\t\t\t<!--[if lte IE 8]>\n" +
                "\t\t\t\t<div class=\"error\"><p>&#3232;_&#3232; <strong>You're using an old version of Internet Explorer.</strong></p>\n" +
                "\t\t\t\t<p>We use some transparent backgrounds, rounded corners, and other effects that your old version of IE doesn't support.</p>\n" +
                "\t\t\t\t<p>Please install <em>one</em> of these: <a href=\"http://www.google.com/chrome\">Chrome</a> | <a href=\"http://www.mozilla.org/en-US/firefox/\">Firefox</a> | <a href=\"http://windows.microsoft.com/en-US/internet-explorer/products/ie/home\">Internet Explorer 9</a></p></div>\n" +
                "\t\t\t<![endif]-->\n" +
                "\n" +
                "\t\t\t\n" +
                "\t\t\t<pre class=\"urlbox\" style=\"word-wrap: break-word;\">https://replay.pokemonshowdown.com/gen8ou-1153524552</pre>\n" +
                "\n" +
                "\t\t\t<h1 style=\"font-weight:normal;text-align:left\"><strong>[Gen 8] OU</strong>: <a href=\"//replay.pokemonshowdown.com/kyxor\" class=\"subtle\">Kyxor</a> vs. <a href=\"//replay.pokemonshowdown.com/stormzone\" class=\"subtle\">Storm Zone</a></h1>\n" +
                "\t\t\t<p style=\"padding:0 1em;margin-top:0\">\n" +
                "\t\t\t\t<small class=\"uploaddate\" data-timestamp=\"1595099780\"><em>Uploaded:</em> Jul 18, 2020</small>\n" +
                "\t\t\t</p>\n" +
                "\n" +
                "\t\t\t<div id=\"loopcount\"></div>\n" +
                "\t\t</div>\n" +
                "\n" +
                "\t\t<input type=\"hidden\" name=\"replayid\" value=\"gen8ou-1153524552\" />\n" +
                "\t\t<!--\n" +
                "\n" +
                "You can get this log directly at https://replay.pokemonshowdown.com/gen8ou-1153524552.log\n" +
                "\n" +
                "Or with metadata at https://replay.pokemonshowdown.com/gen8ou-1153524552.json\n" +
                "\n" +
                "Most PS pages you'd want to scrape will have a .json version!\n" +
                "\n" +
                "\t\t-->\n" +
                "\t\t<script type=\"text/plain\" class=\"log\">|j|☆Kyxor\n" +
                "|j|☆Storm Zone\n" +
                "|inactive|Battle timer is ON: inactive players will automatically lose when time's up.\n" +
                "|player|p1|Kyxor|blackbelt|\n" +
                "|player|p2|Storm Zone|lusamine-nihilego|\n" +
                "|teamsize|p1|6\n" +
                "|teamsize|p2|6\n" +
                "|gametype|singles\n" +
                "|gen|8\n" +
                "|tier|[Gen 8] OU\n" +
                "|rated|Tournament battle\n" +
                "|clearpoke\n" +
                "|poke|p1|Ferrothorn, F|\n" +
                "|poke|p1|Rillaboom, F|\n" +
                "|poke|p1|Corviknight, F|\n" +
                "|poke|p1|Cinderace, M|\n" +
                "|poke|p1|Kommo-o, M|\n" +
                "|poke|p1|Zeraora|\n" +
                "|poke|p2|Haxorus, M|\n" +
                "|poke|p2|Primarina, M|\n" +
                "|poke|p2|Ferrothorn, M|\n" +
                "|poke|p2|Volcarona, M|\n" +
                "|poke|p2|Diggersby, M|\n" +
                "|poke|p2|Hydreigon, M|\n" +
                "|rule|Sleep Clause Mod: Limit one foe put to sleep\n" +
                "|rule|Species Clause: Limit one of each Pokémon\n" +
                "|rule|OHKO Clause: OHKO moves are banned\n" +
                "|rule|Evasion Moves Clause: Evasion moves are banned\n" +
                "|rule|Endless Battle Clause: Forcing endless battles is banned\n" +
                "|rule|HP Percentage Mod: HP is shown in percentages\n" +
                "|rule|Dynamax Clause: You cannot dynamax\n" +
                "|teampreview\n" +
                "|j| hipster garbage\n" +
                "|j| Dread Arceus\n" +
                "|inactive|Kyxor has 120 seconds left.\n" +
                "|\n" +
                "|start\n" +
                "|switch|p1a: Rillaboom|Rillaboom, F|100\\/100\n" +
                "|switch|p2a: Predator|Haxorus, M|100\\/100\n" +
                "|-ability|p2a: Predator|Mold Breaker\n" +
                "|-fieldstart|move: Grassy Terrain|[from] ability: Grassy Surge|[of] p1a: Rillaboom\n" +
                "|-enditem|p1a: Rillaboom|Grassy Seed\n" +
                "|-boost|p1a: Rillaboom|def|1|[from] item: Grassy Seed\n" +
                "|turn|1\n" +
                "|j|%Primal Unova\n" +
                "|j| I Eat Snoms\n" +
                "|\n" +
                "|move|p1a: Rillaboom|Fake Out|p2a: Predator\n" +
                "|-damage|p2a: Predator|84\\/100\n" +
                "|cant|p2a: Predator|flinch\n" +
                "|\n" +
                "|-heal|p2a: Predator|90\\/100|[from] Grassy Terrain\n" +
                "|upkeep\n" +
                "|turn|2\n" +
                "|j| Larmes D'Ange\n" +
                "|\n" +
                "|switch|p2a: Hybrid|Hydreigon, M|100\\/100\n" +
                "|move|p1a: Rillaboom|High Horsepower|p2a: Hybrid\n" +
                "|-immune|p2a: Hybrid|[from] ability: Levitate\n" +
                "|\n" +
                "|upkeep\n" +
                "|turn|3\n" +
                "|j|+Skypenguin\n" +
                "|\n" +
                "|switch|p1a: Ferrothorn|Ferrothorn, F|100\\/100\n" +
                "|move|p2a: Hybrid|Flamethrower|p1a: Ferrothorn\n" +
                "|-supereffective|p1a: Ferrothorn\n" +
                "|-damage|p1a: Ferrothorn|0 fnt\n" +
                "|faint|p1a: Ferrothorn\n" +
                "|\n" +
                "|upkeep\n" +
                "|\n" +
                "|switch|p1a: Cinderace|Cinderace, M|100\\/100\n" +
                "|turn|4\n" +
                "|\n" +
                "|switch|p2a: Happiness|Primarina, M|100\\/100\n" +
                "|move|p1a: Cinderace|High Jump Kick|p2a: Happiness\n" +
                "|-start|p1a: Cinderace|typechange|Fighting|[from] ability: Libero\n" +
                "|-resisted|p2a: Happiness\n" +
                "|-damage|p2a: Happiness|60\\/100\n" +
                "|\n" +
                "|-heal|p2a: Happiness|66\\/100|[from] item: Leftovers\n" +
                "|-heal|p2a: Happiness|72\\/100|[from] Grassy Terrain\n" +
                "|upkeep\n" +
                "|turn|5\n" +
                "|\n" +
                "|switch|p1a: Rillaboom|Rillaboom, F|100\\/100\n" +
                "|move|p2a: Happiness|Draining Kiss|p1a: Rillaboom\n" +
                "|-damage|p1a: Rillaboom|74\\/100\n" +
                "|-heal|p2a: Happiness|91\\/100|[from] drain|[of] p1a: Rillaboom\n" +
                "|\n" +
                "|-heal|p1a: Rillaboom|80\\/100|[from] Grassy Terrain\n" +
                "|-heal|p2a: Happiness|97\\/100|[from] item: Leftovers\n" +
                "|-heal|p2a: Happiness|100\\/100|[from] Grassy Terrain\n" +
                "|-fieldend|move: Grassy Terrain\n" +
                "|upkeep\n" +
                "|turn|6\n" +
                "|j| venson04\n" +
                "|\n" +
                "|switch|p2a: Temple|Ferrothorn, M|100\\/100\n" +
                "|move|p1a: Rillaboom|Fake Out|p2a: Temple\n" +
                "|-resisted|p2a: Temple\n" +
                "|-damage|p2a: Temple|96\\/100\n" +
                "|-damage|p1a: Rillaboom|68\\/100|[from] ability: Iron Barbs|[of] p2a: Temple\n" +
                "|\n" +
                "|-heal|p2a: Temple|100\\/100|[from] item: Leftovers\n" +
                "|upkeep\n" +
                "|turn|7\n" +
                "|\n" +
                "|move|p1a: Rillaboom|High Horsepower|p2a: Temple\n" +
                "|-damage|p2a: Temple|77\\/100\n" +
                "|-damage|p1a: Rillaboom|55\\/100|[from] ability: Iron Barbs|[of] p2a: Temple\n" +
                "|move|p2a: Temple|Knock Off|p1a: Rillaboom\n" +
                "|-damage|p1a: Rillaboom|40\\/100\n" +
                "|\n" +
                "|-heal|p2a: Temple|83\\/100|[from] item: Leftovers\n" +
                "|upkeep\n" +
                "|turn|8\n" +
                "|j| HYper\n" +
                "|c|%Primal Unova|yknow, you can always just give him a chance Storm Zone\n" +
                "|c|☆Kyxor|why did i just have a deja vu-\n" +
                "|c| HYper|Kyxor shouldve brought band mold breaker drill smh\n" +
                "|c|☆Storm Zone|he has one tho\n" +
                "|inactive|Kyxor has 120 seconds left.\n" +
                "|\n" +
                "|move|p1a: Rillaboom|High Horsepower|p2a: Temple|[miss]\n" +
                "|-miss|p1a: Rillaboom|p2a: Temple\n" +
                "|move|p2a: Temple|Knock Off|p1a: Rillaboom\n" +
                "|-damage|p1a: Rillaboom|25\\/100\n" +
                "|\n" +
                "|-heal|p2a: Temple|90\\/100|[from] item: Leftovers\n" +
                "|upkeep\n" +
                "|turn|9\n" +
                "|\n" +
                "|move|p1a: Rillaboom|High Horsepower|p2a: Temple|[miss]\n" +
                "|-miss|p1a: Rillaboom|p2a: Temple\n" +
                "|move|p2a: Temple|Stealth Rock|p1a: Rillaboom\n" +
                "|-sidestart|p1: Kyxor|move: Stealth Rock\n" +
                "|\n" +
                "|-heal|p2a: Temple|96\\/100|[from] item: Leftovers\n" +
                "|upkeep\n" +
                "|turn|10\n" +
                "|\n" +
                "|move|p1a: Rillaboom|High Horsepower|p2a: Temple\n" +
                "|-damage|p2a: Temple|74\\/100\n" +
                "|-damage|p1a: Rillaboom|13\\/100|[from] ability: Iron Barbs|[of] p2a: Temple\n" +
                "|move|p2a: Temple|Stealth Rock||[still]\n" +
                "|-fail|p2a: Temple\n" +
                "|\n" +
                "|-heal|p2a: Temple|80\\/100|[from] item: Leftovers\n" +
                "|upkeep\n" +
                "|turn|11\n" +
                "|c|%Primal Unova|you sure\n" +
                "|\n" +
                "|move|p1a: Rillaboom|High Horsepower|p2a: Temple\n" +
                "|-damage|p2a: Temple|56\\/100\n" +
                "|-damage|p1a: Rillaboom|0 fnt|[from] ability: Iron Barbs|[of] p2a: Temple\n" +
                "|faint|p1a: Rillaboom\n" +
                "|move|p2a: Temple|Stealth Rock||[still]\n" +
                "|-fail|p2a: Temple\n" +
                "|\n" +
                "|-heal|p2a: Temple|63\\/100|[from] item: Leftovers\n" +
                "|upkeep\n" +
                "|j| Bike Mike\n" +
                "|c|☆Storm Zone|fine ill let him chip ferro\n" +
                "|c|%Primal Unova|doesn't look like it\n" +
                "|\n" +
                "|switch|p1a: Cinderace|Cinderace, M|100\\/100\n" +
                "|-damage|p1a: Cinderace|76\\/100|[from] Stealth Rock\n" +
                "|turn|12\n" +
                "|j|%gomikyu\n" +
                "|\n" +
                "|move|p1a: Cinderace|Pyro Ball|p2a: Temple\n" +
                "|-supereffective|p2a: Temple\n" +
                "|-damage|p2a: Temple|0 fnt\n" +
                "|faint|p2a: Temple\n" +
                "|\n" +
                "|upkeep\n" +
                "|\n" +
                "|switch|p2a: Happiness|Primarina, M|100\\/100\n" +
                "|turn|13\n" +
                "|\n" +
                "|switch|p1a: Corviknight|Corviknight, F|100\\/100\n" +
                "|-damage|p1a: Corviknight|88\\/100|[from] Stealth Rock\n" +
                "|-ability|p1a: Corviknight|Pressure\n" +
                "|move|p2a: Happiness|Scald|p1a: Corviknight\n" +
                "|-damage|p1a: Corviknight|56\\/100\n" +
                "|-status|p1a: Corviknight|brn\n" +
                "|\n" +
                "|-damage|p1a: Corviknight|50\\/100 brn|[from] brn\n" +
                "|upkeep\n" +
                "|turn|14\n" +
                "|\n" +
                "|move|p1a: Corviknight|Tailwind|p1a: Corviknight\n" +
                "|-sidestart|p1: Kyxor|move: Tailwind\n" +
                "|move|p2a: Happiness|Substitute|p2a: Happiness\n" +
                "|-start|p2a: Happiness|Substitute\n" +
                "|-damage|p2a: Happiness|75\\/100\n" +
                "|\n" +
                "|-heal|p2a: Happiness|82\\/100|[from] item: Leftovers\n" +
                "|-damage|p1a: Corviknight|44\\/100 brn|[from] brn\n" +
                "|upkeep\n" +
                "|turn|15\n" +
                "|l| hipster garbage\n" +
                "|\n" +
                "|move|p1a: Corviknight|Iron Head|p2a: Happiness\n" +
                "|-activate|p2a: Happiness|move: Substitute|[damage]\n" +
                "|move|p2a: Happiness|Calm Mind|p2a: Happiness\n" +
                "|-boost|p2a: Happiness|spa|1\n" +
                "|-boost|p2a: Happiness|spd|1\n" +
                "|\n" +
                "|-heal|p2a: Happiness|88\\/100|[from] item: Leftovers\n" +
                "|-damage|p1a: Corviknight|38\\/100 brn|[from] brn\n" +
                "|upkeep\n" +
                "|turn|16\n" +
                "|\n" +
                "|move|p1a: Corviknight|Iron Head|p2a: Happiness\n" +
                "|-end|p2a: Happiness|Substitute\n" +
                "|move|p2a: Happiness|Calm Mind|p2a: Happiness\n" +
                "|-boost|p2a: Happiness|spa|1\n" +
                "|-boost|p2a: Happiness|spd|1\n" +
                "|\n" +
                "|-heal|p2a: Happiness|94\\/100|[from] item: Leftovers\n" +
                "|-damage|p1a: Corviknight|32\\/100 brn|[from] brn\n" +
                "|upkeep\n" +
                "|turn|17\n" +
                "|\n" +
                "|move|p1a: Corviknight|Brave Bird|p2a: Happiness\n" +
                "|-damage|p2a: Happiness|73\\/100\n" +
                "|-damage|p1a: Corviknight|26\\/100 brn|[from] Recoil\n" +
                "|move|p2a: Happiness|Substitute|p2a: Happiness\n" +
                "|-start|p2a: Happiness|Substitute\n" +
                "|-damage|p2a: Happiness|48\\/100\n" +
                "|\n" +
                "|-heal|p2a: Happiness|54\\/100|[from] item: Leftovers\n" +
                "|-damage|p1a: Corviknight|20\\/100 brn|[from] brn\n" +
                "|-sideend|p1: Kyxor|move: Tailwind\n" +
                "|upkeep\n" +
                "|turn|18\n" +
                "|\n" +
                "|move|p1a: Corviknight|Brave Bird|p2a: Happiness\n" +
                "|-activate|p2a: Happiness|move: Substitute|[damage]\n" +
                "|-damage|p1a: Corviknight|14\\/100 brn|[from] Recoil|[of] p2a: Happiness\n" +
                "|move|p2a: Happiness|Draining Kiss|p1a: Corviknight\n" +
                "|-resisted|p1a: Corviknight\n" +
                "|-damage|p1a: Corviknight|0 fnt\n" +
                "|-heal|p2a: Happiness|65\\/100|[from] drain|[of] p1a: Corviknight\n" +
                "|-damage|p2a: Happiness|48\\/100|[from] item: Rocky Helmet|[of] p1a: Corviknight\n" +
                "|faint|p1a: Corviknight\n" +
                "|\n" +
                "|-heal|p2a: Happiness|54\\/100|[from] item: Leftovers\n" +
                "|upkeep\n" +
                "|\n" +
                "|switch|p1a: Zeraora|Zeraora|100\\/100\n" +
                "|-damage|p1a: Zeraora|88\\/100|[from] Stealth Rock\n" +
                "|turn|19\n" +
                "|\n" +
                "|move|p1a: Zeraora|Plasma Fists|p2a: Happiness\n" +
                "|-supereffective|p2a: Happiness\n" +
                "|-end|p2a: Happiness|Substitute\n" +
                "|-heal|p1a: Zeraora|89\\/100|[from] item: Shell Bell|[of] p2a: Happiness\n" +
                "|move|p2a: Happiness|Draining Kiss|p1a: Zeraora\n" +
                "|-damage|p1a: Zeraora|36\\/100\n" +
                "|-heal|p2a: Happiness|89\\/100|[from] drain|[of] p1a: Zeraora\n" +
                "|\n" +
                "|-heal|p2a: Happiness|95\\/100|[from] item: Leftovers\n" +
                "|upkeep\n" +
                "|turn|20\n" +
                "|\n" +
                "|move|p1a: Zeraora|Plasma Fists|p2a: Happiness\n" +
                "|-supereffective|p2a: Happiness\n" +
                "|-damage|p2a: Happiness|20\\/100\n" +
                "|-fieldactivate|move: Ion Deluge\n" +
                "|-heal|p1a: Zeraora|47\\/100|[from] item: Shell Bell|[of] p2a: Happiness\n" +
                "|move|p2a: Happiness|Draining Kiss|p1a: Zeraora\n" +
                "|-damage|p1a: Zeraora|0 fnt\n" +
                "|-heal|p2a: Happiness|50\\/100|[from] drain|[of] p1a: Zeraora\n" +
                "|faint|p1a: Zeraora\n" +
                "|\n" +
                "|-heal|p2a: Happiness|57\\/100|[from] item: Leftovers\n" +
                "|upkeep\n" +
                "|l| Bike Mike\n" +
                "|\n" +
                "|switch|p1a: Cinderace|Cinderace, M|76\\/100\n" +
                "|-damage|p1a: Cinderace|51\\/100|[from] Stealth Rock\n" +
                "|turn|21\n" +
                "|\n" +
                "|move|p1a: Cinderace|Gunk Shot|p2a: Happiness\n" +
                "|-start|p1a: Cinderace|typechange|Poison|[from] ability: Libero\n" +
                "|-supereffective|p2a: Happiness\n" +
                "|-damage|p2a: Happiness|0 fnt\n" +
                "|faint|p2a: Happiness\n" +
                "|\n" +
                "|upkeep\n" +
                "|\n" +
                "|switch|p2a: Lumberjack|Diggersby, M|100\\/100\n" +
                "|turn|22\n" +
                "|\n" +
                "|switch|p1a: Kommo-o|Kommo-o, M|100\\/100\n" +
                "|-damage|p1a: Kommo-o|94\\/100|[from] Stealth Rock\n" +
                "|move|p2a: Lumberjack|Swords Dance|p2a: Lumberjack\n" +
                "|-boost|p2a: Lumberjack|atk|2\n" +
                "|\n" +
                "|upkeep\n" +
                "|turn|23\n" +
                "|\n" +
                "|move|p1a: Kommo-o|Clangorous Soul|p1a: Kommo-o\n" +
                "|-boost|p1a: Kommo-o|atk|1\n" +
                "|-boost|p1a: Kommo-o|def|1\n" +
                "|-boost|p1a: Kommo-o|spa|1\n" +
                "|-boost|p1a: Kommo-o|spd|1\n" +
                "|-boost|p1a: Kommo-o|spe|1\n" +
                "|-damage|p1a: Kommo-o|61\\/100\n" +
                "|move|p2a: Lumberjack|Earthquake|p1a: Kommo-o\n" +
                "|-crit|p1a: Kommo-o\n" +
                "|-damage|p1a: Kommo-o|0 fnt\n" +
                "|-damage|p2a: Lumberjack|91\\/100|[from] item: Life Orb\n" +
                "|faint|p1a: Kommo-o\n" +
                "|\n" +
                "|upkeep\n" +
                "|\n" +
                "|switch|p1a: Cinderace|Cinderace, M|51\\/100\n" +
                "|-damage|p1a: Cinderace|26\\/100|[from] Stealth Rock\n" +
                "|turn|24\n" +
                "|\n" +
                "|move|p2a: Lumberjack|Quick Attack|p1a: Cinderace\n" +
                "|-damage|p1a: Cinderace|0 fnt\n" +
                "|-damage|p2a: Lumberjack|81\\/100|[from] item: Life Orb\n" +
                "|faint|p1a: Cinderace\n" +
                "|\n" +
                "|win|Storm Zone\n" +
                "|l|☆Storm Zone\n" +
                "|c|☆Kyxor|gg\n" +
                "|l| Larmes D'Ange\n" +
                "|l|+Primal Unova\n" +
                "|l| HYper\n" +
                "|l| Skypenguin\n" +
                "|l| gomikyu\n" +
                "</script>\n" +
                "\n" +
                "<div><script type=\"text/javascript\"><!--\n" +
                "google_ad_client = \"ca-pub-6535472412829264\";\n" +
                "/* PS replay */\n" +
                "google_ad_slot = \"6865298132\";\n" +
                "google_ad_width = 728;\n" +
                "google_ad_height = 90;\n" +
                "//-->\n" +
                "</script>\n" +
                "<script type=\"text/javascript\"\n" +
                "src=\"//pagead2.googlesyndication.com/pagead/show_ads.js\">\n" +
                "</script></div>\n" +
                "\n" +
                "\t\t<a href=\"/\" class=\"pfx-backbutton\" data-target=\"back\"><i class=\"fa fa-chevron-left\"></i> More replays</a>\n" +
                "\n" +
                "\t</div></div>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/js/lib/jquery-1.11.0.min.js?8fc25e27\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/js/lib/lodash.core.js?e9be4c2d\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/js/lib/backbone.js?8a8d8296\"></script>\n" +
                "\t<script src=\"//dex.pokemonshowdown.com/js/panels.js?0.31251708905664044\"></script>\n" +
                "\n" +
                "\t<script src=\"//play.pokemonshowdown.com/js/lib/jquery-cookie.js?38477214\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/js/lib/html-sanitizer-minified.js?949c4200\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/js/battle-sound.js?4dcdc9b5\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/config/config.js?6f55c5f6\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/js/battledata.js?ec203117\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/data/pokedex-mini.js?f9992121\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/data/pokedex-mini-bw.js?4c42d9f1\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/data/graphics.js?9cb31a46\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/data/pokedex.js?34f10d54\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/data/items.js?2b217bae\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/data/moves.js?64e80f65\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/data/abilities.js?6f27f7be\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/data/teambuilder-tables.js?b700bd1a\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/js/battle-tooltips.js?2b69e503\"></script>\n" +
                "\t<script src=\"//play.pokemonshowdown.com/js/battle.js?7324c8f4\"></script>\n" +
                "\t<script src=\"/js/replay.js?6887ea68\"></script>\n" +
                "\n" +
                "</body></html>\n");
        htmlTeamExtracter.extractHighLight(html);
    }
}

