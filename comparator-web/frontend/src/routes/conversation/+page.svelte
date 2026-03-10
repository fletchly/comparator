<script lang="ts">
	import { invalidate } from '$app/navigation';
	import { clearAllConversations } from '$lib/api';
	import { resolve } from '$app/paths';
	import type { Conversation } from '$lib/types';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

	const entries = $derived(Object.entries(data.conversations) as [string, Conversation][]);

	function displayName(id: string, conversation: Conversation): string {
		return conversation.displayName ?? id;
	}

	async function handleClearAll() {
		await clearAllConversations();
		await invalidate('app:conversations');
	}
</script>

<h1>Conversations</h1>

<button onclick={handleClearAll}>Clear all</button>

{#each entries as [id, conversation] (id)}
	<section>
		<h2><a href={resolve('/conversation/[id]', { id })}>{displayName(id, conversation)}</a></h2>
		<p>{conversation.messages.length} message{conversation.messages.length === 1 ? '' : 's'}</p>
	</section>
{:else}
	<p>No conversations.</p>
{/each}
