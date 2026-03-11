<!--
  Message.svelte
  Renders a single message of type user | assistant | tool.
-->
<script lang="ts">
	import type { Message } from '$lib/types';
	import ToolCallBadge from './ToolCallBadge.svelte';

	interface Props {
		message: Message;
	}

	let { message }: Props = $props();

	const typeStyles: Record<string, string> = {
		user: 'border-l-secondary',
		assistant: 'border-l-primary',
		tool: 'border-l-success'
	};

	const actorStyles: Record<string, string> = {
		user: 'text-secondary',
		assistant: 'text-primary',
		tool: 'text-success'
	};

	const type = $derived(message.type);
</script>

<article
	data-message-type={type}
	class="border-l-2 bg-background-secondary px-4 py-3 font-mono {typeStyles[type]}"
>
	{#if message.type === 'user'}
		<header class="mb-1 flex items-center gap-2">
			<span
				data-role="type-label"
				class="text-[10px] tracking-widest text-foreground-muted uppercase">user</span
			>
			<span data-role="actor" class="text-xs font-semibold {actorStyles['user']}"
				>{message.name}</span
			>
		</header>
		<p data-role="content" class="text-sm leading-relaxed text-foreground">{message.content}</p>
	{:else if message.type === 'assistant'}
		<header class="mb-1 flex items-center gap-2">
			<span
				data-role="type-label"
				class="text-[10px] tracking-widest text-foreground-muted uppercase">assistant</span
			>
			<span data-role="actor" class="text-xs font-semibold {actorStyles['assistant']}">AI</span>
		</header>
		{#if message.content}
			<p data-role="content" class="text-sm leading-relaxed text-foreground">{message.content}</p>
		{/if}
		{#if message.toolCalls && message.toolCalls.length > 0}
			<ul data-role="tool-calls" class="mt-2 flex flex-col gap-1 text-sm opacity-80">
				{#each message.toolCalls as call (call.name)}
					<li><ToolCallBadge toolCall={call} /></li>
				{/each}
			</ul>
		{/if}
	{:else if message.type === 'tool'}
		<header class="mb-1 flex items-center gap-2">
			<span
				data-role="type-label"
				class="text-[10px] tracking-widest text-foreground-muted uppercase">tool</span
			>
			<span data-role="actor" class="text-xs font-semibold {actorStyles['tool']}"
				>{message.name}</span
			>
		</header>
		<p data-role="content" class="text-sm leading-relaxed text-foreground opacity-80">
			{message.content}
		</p>
	{/if}
</article>
