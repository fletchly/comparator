<!--
  Message.svelte
  Renders a single message of type user | assistant | tool.
  Intentionally unstyled — apply Tailwind classes via props or wrap in a styled shell.
-->
<script lang="ts">
	import type { Message } from '$lib/types';
	import ToolCallBadge from './ToolCallBadge.svelte';

	interface Props {
		message: Message;
	}

	let { message }: Props = $props();
</script>

<article data-message-type={message.type}>
	{#if message.type === 'user'}
		<header>
			<span data-role="actor">{message.name}</span>
		</header>
		<p data-role="content">{message.content}</p>
	{:else if message.type === 'assistant'}
		<header>
			<span data-role="actor">Assistant</span>
		</header>
		{#if message.content}
			<p data-role="content">{message.content}</p>
		{/if}
		{#if message.toolCalls && message.toolCalls.length > 0}
			<ul data-role="tool-calls">
				{#each message.toolCalls as call (call.name)}
					<li><ToolCallBadge toolCall={call} /></li>
				{/each}
			</ul>
		{/if}
	{:else if message.type === 'tool'}
		<header>
			<span data-role="actor">{message.name}</span>
		</header>
		<p data-role="content">{message.content}</p>
	{/if}
</article>
