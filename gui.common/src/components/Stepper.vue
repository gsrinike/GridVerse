<template><ol class="stepper"><li v-for="(step,index) in steps" :key="step" :class="{active:index===active,done:index<active}">{{ index+1 }}. {{ step }}</li></ol></template>
<script setup lang="ts">defineProps<{steps:string[],active:number}>()</script>
