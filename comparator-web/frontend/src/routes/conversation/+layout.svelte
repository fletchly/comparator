<script lang="ts">
	import { onMount } from 'svelte';
	import { invalidate } from '$app/navigation';
	import type { Snippet } from 'svelte';
	import type { LayoutData } from './$types';

	// eslint-disable-next-line @typescript-eslint/no-unused-vars
	let { data: _data, children }: { data: LayoutData; children: Snippet } = $props();

	onMount(() => {
		const source = new EventSource('/api/events');

		source.addEventListener('message', () => invalidate('app:conversations'));
		source.addEventListener('cleared', () => invalidate('app:conversations'));
		source.addEventListener('all-cleared', () => invalidate('app:conversations'));
		source.onerror = () => source.close();

		return () => source.close();
	});
</script>

{@render children()}
