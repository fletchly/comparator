<script lang="ts">
	import { invalidate } from '$app/navigation';
	import { clearConsoleConversation } from '$lib/api';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

	async function handleClear() {
		await clearConsoleConversation();
		await invalidate('app:conversations');
	}
</script>

<h1>Console Conversation</h1>

<button onclick={handleClear}>Clear</button>

{#each data.messages as message (message)}
	<section>
		{#if message.type === 'user'}
			<p><strong>{message.name}:</strong> {message.content}</p>
		{:else if message.type === 'assistant'}
			<p><strong>Assistant:</strong> {message.content}</p>
			{#if message.toolCalls}
				{#each message.toolCalls as call (call.name)}
					<p><em>Tool call: {call.name}</em></p>
				{/each}
			{/if}
		{:else if message.type === 'tool'}
			<p><strong>Tool ({message.name}):</strong> {message.content}</p>
		{/if}
	</section>
{:else}
	<p>No messages.</p>
{/each}
