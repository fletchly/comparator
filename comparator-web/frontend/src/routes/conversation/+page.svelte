<script lang="ts">
	import { invalidate } from '$app/navigation';
	import { clearAllConversations } from '$lib/api';
	import { resolve } from '$app/paths';
	import type { PageData } from './$types';
	import type { Message } from '$lib/types';

	let { data }: { data: PageData } = $props();

	const entries = $derived(Object.entries(data.conversations) as [string, Message[]][]);

	function displayName(id: string): string {
		if (id === data.wellKnown.console) return 'Console';
		if (id === data.wellKnown.chat) return 'Chat';
		return id;
	}

	async function handleClearAll() {
		await clearAllConversations();
		await invalidate('app:conversations');
	}
</script>

<h1>Conversations</h1>

<button onclick={handleClearAll}>Clear all</button>

{#each entries as [id, messages] (id)}
	<section>
		<h2><a href={resolve('/conversation/[id]', { id })}>{displayName(id)}</a></h2>
		<p>{messages.length} message{messages.length === 1 ? '' : 's'}</p>
	</section>
{:else}
	<p>No conversations.</p>
{/each}
