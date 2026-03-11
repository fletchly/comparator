<!--
  ConversationView.svelte
  Renders a conversation's messages with a heading and clear button.
-->
<script lang="ts">
	import Message from '$lib/components/Message.svelte';
	import PageHeading from '$lib/components/ui/PageHeading.svelte';
	import Button from '$lib/components/ui/Button.svelte';
	import { Trash } from '@lucide/svelte';
	import type { Message as MessageType } from '$lib/types';

	interface Props {
		heading: string;
		messages: MessageType[];
		onClear: () => Promise<void>;
	}

	let { heading, messages, onClear }: Props = $props();
</script>

<PageHeading {heading} />

<div class="mb-4 flex items-center justify-between">
	<span class="font-mono text-sm text-muted-light">
		{messages.length} message{messages.length === 1 ? '' : 's'}
	</span>
	<Button onclick={onClear} variant="destructive">
		<Trash class="inline h-[1em] w-[1em] align-[-0.1em]" /> Clear
	</Button>
</div>

<div class="flex flex-col gap-2">
	{#each messages as message (message)}
		<Message {message} />
	{:else}
		<p class="font-mono text-sm text-muted-light">No messages.</p>
	{/each}
</div>
