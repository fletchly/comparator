<script lang="ts">
	import { type ComponentProps } from 'svelte';
	import Sidebar from './Sidebar.svelte';

	type Args = ComponentProps<typeof Sidebar>;

	let { args }: { args: Args } = $props();

	let isMobile = $state(false);
</script>

<div class="relative flex h-screen">
	<Sidebar {...args as Args} bind:isMobile />
	<main class="flex-1 overflow-auto p-6" style:padding-left={isMobile ? '3.5rem' : undefined}>
		<p class="text-sm text-gray-400">Main content area</p>
	</main>
</div>
