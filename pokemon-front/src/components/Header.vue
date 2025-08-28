<script setup>
import Navi from '@/components/Navi.vue'
import Sidebar from 'primevue/sidebar';
import {ref, defineExpose} from "vue";
import Divider from "primevue/divider";

const apiUrl = import.meta.env.VITE_BACKEND_URL;

defineProps({
  showUpdateDate: String
})
const updateDate = ref(null)
const visible = ref()
const headerRef = ref()

async function queryUpdateDate() {
  const res = await fetch(`${apiUrl}/api/rank/update-time`, {
        method: "GET"
      }
  )
  const response = await res.json()
  updateDate.value = response.date
}

queryUpdateDate()

defineExpose({"ref": headerRef});

</script>

<template>
  <header ref="headerRef">
    <Navi class="Navi"></Navi>

    <div class="global-info" v-if="showUpdateDate">
      <p class="max-sm:hidden">{{ $t('Recent update Date') + updateDate }}</p>
      <i class="pi pi-question-circle ml-1 cursor-pointer" @click="visible = true"/>
      <Sidebar v-model:visible="visible" header="关于本站" position="right">
        <p>一个整合<span class="font-bold">PokemonShowdown</span>相关数据及资源的网站，以<span
            class="font-bold">Gen9 OU</span>为主。</p>
        <br/>
        <Divider type="solid" class="mb-2"/>

        <p class="font-bold">数据更新时间：</p>
        <p>1. 每天早上9点更新Ladder</p>
        <br/>

        <p>2. 每月2号早上8点15分更新分级统计</p>
        <br/>

        <p>3. 不定期更新Tour队伍，一般在主办方更新轮次后的一天内</p>
        <br/>

        <p>4. 不定期更新Smogon Analysis、PokePaste等数据</p>
        <br/>
        <Divider type="solid" class="mb-2"/>

        <p class="font-bold">本网站使用了以下开源项目 ：</p>
        <p>1. <a target="_blank" href="https://github.com/pkmn/smogon">@pkmn</a></p>
        <p>2. <a target="_blank"
                 href="https://pschina.one/topic/2087/%E6%B1%89%E5%8C%96%E6%95%99%E7%A8%8B%E4%B8%8E%E5%8F%8D%E9%A6%88">PS
          China 汉化脚本</a></p>
        <br/>

        <p class="font-bold">感谢各位朋友的支持~</p>
        <p class="font-bold">特别感谢 ：</p>
        <p class="font-mono">- Whitepureloli</p>
        <p class="font-mono">- Xuwueryi</p>
        <p class="font-mono">- HanFong</p>
        <p class="font-mono">- Jesse Pirnat</p>
        <p class="font-mono">- Apple</p>
        <br/>

        <p>
          如果有相关疑问或建议，请在b站私信
          <a target="_blank" :href="`https://space.bilibili.com/37832209`">
            <span class="text-[#80DAF6] font-mono">mimosa</span>
          </a>
        </p>
        <br/>

        <Divider type="solid" class="mb-2"/>
        <p class="font-bold">Release Note：</p>
        <p><span class="font-bold">2025/07/25 优化：文本国际化</span></p>
        <br/>

        <p><span class="font-bold">2025/07/21 beta feat：</span>
          可以搜索指定队伍信息(道具、技能、太晶、replay等) <a target="_blank" href="/teamInfo">测试入口(功能已下线)</a>
          <p class="font-bold text-sm">
          </p>
        </p>
        <br/>

        <p><span class="font-bold">2025/07/17 tour：</span>
          <a target="_blank"
             href="https://www.smogon.com/forums/threads/the-world-cup-of-pok%C3%A9mon-2025-replays.3763185/">The World
            Cup of Pokémon 2025</a></p>
        <br/>

        <p><span class="font-bold">2025/06/21 beta feat：</span>支持爬取gen9nationaldex天梯队伍</p>
        <br/>

        <p><span class="font-bold">2025/04/06 feat：</span>支持爬取特性（2025/04/06前已爬取的replay不支持）</p>
        <br/>

        <p><span class="font-bold">2025/03/14 feat：</span>悬浮在正负值上可以查看明细（2025/03/14前已爬取的replay不支持）
        </p>
        <br/>

        <p><span class="font-bold">2025/02/06 优化：</span>显示全部努力值使用率</p>
        <br/>

        <p><span class="font-bold">2025/01/28 优化：</span>夜间模式开关</p>
        <br/>

        <p><span class="font-bold">2025/01/17 tour：</span>
          <a target="_blank" href="https://www.smogon.com/forums/threads/smogon-premier-league-xvi-replays.3758078//">Smogon
            Premier League XVI</a></p>
        <br/>

        <p><span class="font-bold">2025/01/17 优化：</span>UI调整</p>
        <br/>

        <p><span class="font-bold">2025/01/12 feat：</span>新增队伍排序模式：意外性</p>
        <br/>

        <p><span class="font-bold">2025/01/03 优化：</span>可切换对战中变化的形态</p>
        <br/>

        <p><span class="font-bold">2025/01/03 修复：</span>调整第二世代及之前能力值计算公式</p>
        <br/>

        <p><span class="font-bold">2024/12/23 优化：</span>宝可梦努力值右侧显示对应能力值</p>
        <br/>

        <p><span class="font-bold">2024/12/21 feat：</span>悬浮在进攻贡献值上可以查看伤害明细（2024/12/21前已爬取的replay不支持）
        </p>
        <br/>

        <p><span class="font-bold">2024/12/1 beta feat：</span>
          可以查询指定replay的对局统计
          <a target="_blank" href="/battleAnalysis">测试入口</a>
        </p>
        <br/>

        <p><span class="font-bold">2024/11/20 feat：</span>可选择是否只查询有PokePaste的队伍</p>
        <br/>

        <p><span class="font-bold">2024/11/17 feat：</span>使用率排行支持过滤</p>
        <br/>

        <p><span class="font-bold">2024/11/9 优化：</span>系统设置为深色模式时，调整主题色</p>
        <br/>

        <p><span class="font-bold">2024/11/4 tour：</span>
          <a target="_blank" href="https://www.smogon.com/forums/threads/oupl-viii-replays.3754001/">OUPL VIII</a></p>
        <br/>

        <p><span class="font-bold">2024/10/29 修复：</span>正确显示旧世代特性、道具、招式文本，正确显示旧世代宝可梦的属性及种族值
        </p>
        <br/>

        <p><span class="font-bold">2024/10/24 优化：</span>Similar Teams存在对应PokePaste时，队伍右侧显示链接按钮</p>
        <br/>

        <p><span class="font-bold">2024/10/19 优化：</span>调整队伍配置文本为PokePaste格式</p>
      </Sidebar>
    </div>
  </header>
</template>

<style>
header {
  position: fixed;
  top: 0;
  left: 0;
  display: flex;
  z-index: 10;
  background-color: rgb(15 23 42 / var(--tw-bg-opacity, 1));
  justify-content: space-between;
  width: 100%;
}

.global-info {
  color: white;
  display: flex;
  align-items: center;
  margin-right: 15px;
}
</style>