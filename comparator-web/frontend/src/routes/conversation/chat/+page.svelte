<script lang="ts">
	import { invalidate } from '$app/navigation';
	import { clearChatConversation } from '$lib/api';
	import Message from '$lib/components/Message.svelte';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

	async function handleClear() {
		await clearChatConversation();
		await invalidate('app:conversations');
	}
</script>

<h1>Chat Conversation</h1>

<button onclick={handleClear}>Clear</button>

{#each data.messages as message (message)}
	<Message {message} />
{:else}
	<p>No messages.</p>
{/each}
