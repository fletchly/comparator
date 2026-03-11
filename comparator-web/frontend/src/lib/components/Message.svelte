<!--
  Message.svelte
  Renders a single message of type user | assistant | tool.
-->
<script lang="ts">
	import type { Message } from '$lib/types';
	import ToolCallBadge from './ToolCallBadge.svelte';
	import { ChevronDown } from '@lucide/svelte';

	interface Props {
		message: Message;
	}

	let { message }: Props = $props();

	const COLLAPSE_THRESHOLD = 300;

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

	function isLong(text: string) {
		return text.split('\n').length > 5 || text.length > COLLAPSE_THRESHOLD;
	}

	let expanded = $state(false);
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
		<p
			data-role="content"
			class="text-sm leading-relaxed text-foreground {!expanded && isLong(message.content)
				? 'line-clamp-5'
				: ''}"
		>
			{message.content}
		</p>
		{#if isLong(message.content)}
			<button
				onclick={() => (expanded = !expanded)}
				class="mt-1 flex cursor-pointer items-center gap-1 text-xs text-muted-light transition-colors hover:text-foreground"
			>
				<ChevronDown
					size={13}
					class="transition-transform duration-200 {expanded ? 'rotate-180' : ''}"
				/>
				{expanded ? 'Collapse' : 'Expand'}
			</button>
		{/if}
	{:else if message.type === 'assistant'}
		<header class="mb-1 flex items-center gap-2">
			<span
				data-role="type-label"
				class="text-[10px] tracking-widest text-foreground-muted uppercase">comparator</span
			>
			<span data-role="actor" class="text-xs font-semibold {actorStyles['assistant']}">AI</span>
		</header>
		{#if message.content}
			<p
				data-role="content"
				class="text-sm leading-relaxed text-foreground {!expanded && isLong(message.content)
					? 'line-clamp-5'
					: ''}"
			>
				{message.content}
			</p>
			{#if isLong(message.content)}
				<button
					onclick={() => (expanded = !expanded)}
					class="mt-1 flex cursor-pointer items-center gap-1 text-xs text-muted-light transition-colors hover:text-foreground"
				>
					<ChevronDown
						size={13}
						class="transition-transform duration-200 {expanded ? 'rotate-180' : ''}"
					/>
					{expanded ? 'Collapse' : 'Expand'}
				</button>
			{/if}
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
		<p
			data-role="content"
			class="text-sm leading-relaxed text-foreground opacity-80 {!expanded &&
			isLong(message.content)
				? 'line-clamp-5'
				: ''}"
		>
			{message.content}
		</p>
		{#if isLong(message.content)}
			<button
				onclick={() => (expanded = !expanded)}
				class="mt-1 flex cursor-pointer items-center gap-1 text-xs text-muted-light transition-colors hover:text-foreground"
			>
				<ChevronDown
					size={13}
					class="transition-transform duration-200 {expanded ? 'rotate-180' : ''}"
				/>
				{expanded ? 'Collapse' : 'Expand'}
			</button>
		{/if}
	{/if}
</article>
