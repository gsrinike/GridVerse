<template><button class="gv-button" :disabled="disabled"><slot /></button></template>
<script setup lang="ts">defineProps<{disabled?: boolean}>()</script>
