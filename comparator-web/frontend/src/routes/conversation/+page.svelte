<script lang="ts">
	import { invalidateAll } from '$app/navigation';
	import { clearAllConversations } from '$lib/api';
	import { resolve } from '$app/paths';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

	async function handleClearAll() {
		await clearAllConversations();
		await invalidateAll();
	}
</script>

<h1>Conversations</h1>

<button onclick={handleClearAll}>Clear all</button>

{#each Object.entries(data.conversations) as [id, messages] (id)}
	<section>
		<h2><a href={resolve('/conversation/[id]', { id })}>{id}</a></h2>
		<p>{messages.length} message{messages.length === 1 ? '' : 's'}</p>
	</section>
{:else}
	<p>No conversations.</p>
{/each}
