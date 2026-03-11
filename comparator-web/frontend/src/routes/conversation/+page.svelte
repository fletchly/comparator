<script lang="ts">
	import { invalidate } from '$app/navigation';
	import { resolve } from '$app/paths';
	import { clearAllConversations } from '$lib/api';
	import ConversationListItem from '$lib/components/ConversationListItem.svelte';
	import Button from '$lib/components/ui/Button.svelte';
	import { Trash } from '@lucide/svelte';
	import type { Conversation } from '$lib/types';
	import type { PageData } from './$types';
	import PageHeading from '$lib/components/ui/PageHeading.svelte';

	let { data }: { data: PageData } = $props();

	const entries = $derived(Object.entries(data.conversations) as [string, Conversation][]);

	async function handleClearAll() {
		await clearAllConversations();
		await invalidate('app:conversations');
	}
</script>

<PageHeading heading="Conversations" />

<div class="mb-4 flex items-center justify-between">
	<span class="font-mono text-sm text-muted-light"
		>{entries.length} conversation{entries.length === 1 ? '' : 's'}</span
	>
	<Button onclick={handleClearAll} variant="destructive">
		<Trash class="inline h-[1em] w-[1em] align-[-0.1em]" /> Clear All
	</Button>
</div>

<div class="flex flex-col gap-2">
	{#each entries as [id, conversation] (id)}
		<a href={resolve('/conversation/[id]', { id })}>
			<ConversationListItem {id} {conversation} />
		</a>
	{:else}
		<p class="font-mono text-sm text-muted-light">No conversations.</p>
	{/each}
</div>
