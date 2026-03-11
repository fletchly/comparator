<script lang="ts">
	import { invalidate } from '$app/navigation';
	import { clearConversation } from '$lib/api';
	import Message from '$lib/components/Message.svelte';
	import PageHeading from '$lib/components/ui/PageHeading.svelte';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

	async function handleClear() {
		await clearConversation(data.id);
		await invalidate('app:conversations');
	}
</script>

<PageHeading heading={data.displayName ?? data.id} />

<button onclick={handleClear}>Clear</button>

{#each data.messages as message (message)}
	<Message {message} />
{:else}
	<p>No messages.</p>
{/each}
