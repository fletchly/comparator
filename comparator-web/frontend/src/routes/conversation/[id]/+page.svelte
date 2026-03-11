<script lang="ts">
	import { invalidate } from '$app/navigation';
	import { clearConversation } from '$lib/api';
	import ConversationView from '$lib/components/ConversationView.svelte';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

	async function handleClear() {
		await clearConversation(data.id);
		await invalidate('app:conversations');
	}
</script>

<ConversationView
	heading={data.displayName ?? data.id}
	messages={data.messages}
	onClear={handleClear}
/>
