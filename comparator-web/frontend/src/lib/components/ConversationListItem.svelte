<!--
  ConversationListItem.svelte
  A single row in a conversation list.
  Unstyled — wrap in your own <a href={resolveRoute(...)}> in the parent.
-->
<script lang="ts">
	import type { Conversation } from '$lib/types';

	interface Props {
		id: string;
		conversation: Conversation;
	}

	let { id, conversation }: Props = $props();

	const label = $derived(conversation.displayName ?? id);
	const count = $derived(conversation.messages.length);
</script>

<span data-role="conversation-item">
	<span data-role="name">{label}</span>
	<span data-role="count">{count} message{count === 1 ? '' : 's'}</span>
</span>
