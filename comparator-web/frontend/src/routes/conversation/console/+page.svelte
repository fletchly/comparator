<script lang="ts">
	import { invalidate } from '$app/navigation';
	import { clearConsoleConversation } from '$lib/api';
	import ConversationView from '$lib/components/ConversationView.svelte';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

	async function handleClear() {
		await clearConsoleConversation();
		await invalidate('app:conversations');
	}
</script>

<ConversationView heading="Console Conversation" messages={data.messages} onClear={handleClear} />
