<template><span class="badge" :data-state="state.toLowerCase()">{{ state }}</span></template>
<script setup lang="ts">defineProps<{state:string}>()</script>
