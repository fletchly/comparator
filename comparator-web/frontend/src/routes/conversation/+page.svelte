<script lang="ts">
	import { invalidate } from '$app/navigation';
	import { resolve } from '$app/paths';
	import { clearAllConversations } from '$lib/api';
	import ConversationListItem from '$lib/components/ConversationListItem.svelte';
	import type { Conversation } from '$lib/types';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

	const entries = $derived(Object.entries(data.conversations) as [string, Conversation][]);

	async function handleClearAll() {
		await clearAllConversations();
		await invalidate('app:conversations');
	}
</script>

<h1>Conversations</h1>

<button onclick={handleClearAll}>Clear all</button>

{#each entries as [id, conversation] (id)}
	<a href={resolve('/conversation/[id]', { id })}>
		<ConversationListItem {id} {conversation} />
	</a>
{:else}
	<p>No conversations.</p>
{/each}
