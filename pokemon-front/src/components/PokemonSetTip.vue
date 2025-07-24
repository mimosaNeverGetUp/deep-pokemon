<!--
  -  MIT License
  -
  -  Copyright (c) 2025-2025 mimosa
  -->
<script setup>
import {ref} from "vue";
import OverlayPanel from 'primevue/overlaypanel';
import {Dex} from '@pkmn/dex';
import {useI18n} from 'vue-i18n'

const {t} = useI18n()

const props = defineProps({
  pokemon: Object,
  pokemonConfigMap: {
    type: Object,
    required: false
  }
})

const op = ref();
const toggle = (event) => {
  op.value.toggle(event);
};

function getIconUrl(pokemon) {
  if (pokemon.detailChange) {
    const iconName = pokemon.detailChange.replace(" ", "").replace("-*", "");
    return "/pokemonicon/" + encodeURIComponent(iconName) + ".png";
  } else {
    const iconName = pokemon.name.replace(" ", "").replace("-*", "");
    return "/pokemonicon/" + encodeURIComponent(iconName) + ".png";
  }
}

function getItemUrl(item) {
  return "/itemicon/" + item + ".png";
}

function getTeraIcon(tera) {
  return "/types/" + tera + ".png";
}

function getPokemonItemText(pokemon) {
  if (props.pokemonConfigMap) {
    let pokemonConfig = props.pokemonConfigMap[pokemon.name];
    if (pokemonConfig && pokemonConfig.items) {
      return pokemonConfig.items.length === 0 ? "???" : pokemonConfig.items.map(a => t(a)).join("/");
    }
  }
  return pokemon.item == null ? "???" : t(pokemon.item);
}

function getPokemonAbilityText(pokemon) {
  if (uniquePokemonAbility(pokemon.name)) {
    return t(uniquePokemonAbility(pokemon.name));
  }

  if (props.pokemonConfigMap) {
    let pokemonConfig = props.pokemonConfigMap[pokemon.name];
    if (pokemonConfig && pokemonConfig.abilities) {
      return pokemonConfig.abilities.length === 0 ? "???" : pokemonConfig.abilities.map(a => t(a)).join("/");
    }
  }
  return pokemon.ability == null ? "???" : t(pokemon.ability);
}

function uniquePokemonAbility(pokemon) {
  let abilities = Dex.forGen(9).species.get(pokemon)?.abilities;
  if (abilities && Object.keys(abilities).length === 1) {
    let abilityKey = Object.keys(abilities)[0];
    return abilities[abilityKey];
  }
  return null;
}

</script>

<template>
  <div>
    <div>
      <img :src="getIconUrl(pokemon)" :alt="pokemon.name" @mouseover="toggle" @mouseleave="toggle"/>
      <img class="absolute h-4 w-4 bottom-0 right-0" v-if="pokemon.item" :src="getItemUrl(pokemon.item)"
           :alt="pokemon.item"/>
    </div>
    <OverlayPanel ref="op" pt:content:class="bg-[#F0F0F0E6]">
      <div class="min-w-48 text-left text-black">
        <p class="font-bold">
          {{ $t(pokemon.name) }}
        </p>
        <div v-if="pokemon.item">
          <span>{{$t('set tip item')}}</span>
          <span class="font-sans">{{ getPokemonItemText(pokemon) }}</span>
        </div>
        <div v-if="pokemon.teraType" class="flex items-center gap-1">
          <p>{{$t('set tip tera')}}</p>
          <img height="17" width="40" v-for="tera in pokemon.teraType.split('/')" :src="getTeraIcon(tera)"
               :alt="pokemon.teraType" :title="pokemon.teraType">
        </div>
        <div v-if="pokemon.ability || uniquePokemonAbility(pokemon.name)">
          <span>{{$t('set tip ability')}}</span>
          <span class="font-sans">{{ getPokemonAbilityText(pokemon) }}</span>
        </div>
        <div v-if="pokemon.moves && pokemon.moves.length !==0">
          <hr>
          <p v-for="move in pokemon.moves.slice(0, 4)">
            <span class="font-sans">{{ "-" + $t(move) }}</span>
          </p>
          <div class="mt-3" v-if="pokemon.moves.slice(4, pokemon.moves.length).length !==0">
            <hr>
            <p class="text-sm">{{$t('set tip other moves')}}</p>
            <p v-for="move in pokemon.moves.slice(4, pokemon.moves.length)">
              <span class="text-sm font-sans">{{ "-" + $t(move) }}</span>
            </p>
          </div>
        </div>
      </div>
    </OverlayPanel>
  </div>
</template>