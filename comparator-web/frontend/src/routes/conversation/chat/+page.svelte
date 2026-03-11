<script lang="ts">
	import { invalidate } from '$app/navigation';
	import { clearChatConversation } from '$lib/api';
	import ConversationView from '$lib/components/ConversationView.svelte';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

	async function handleClear() {
		await clearChatConversation();
		await invalidate('app:conversations');
	}
</script>

<ConversationView heading="Chat Conversation" messages={data.messages} onClear={handleClear} />
