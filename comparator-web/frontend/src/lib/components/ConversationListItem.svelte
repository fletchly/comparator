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

<span
	data-role="conversation-item"
	class="group flex items-center justify-between border border-muted bg-background-secondary px-4 py-3 font-mono transition-colors duration-150 hover:border-primary hover:bg-primary-tint"
>
	<span
		data-role="name"
		class="truncate text-sm text-foreground transition-colors group-hover:text-primary"
	>
		{label}
	</span>
	<span
		data-role="count"
		class="ml-4 shrink-0 text-xs text-muted-light transition-colors group-hover:text-foreground"
	>
		{count}&nbsp;msg{count === 1 ? '' : 's'}
	</span>
</span>
