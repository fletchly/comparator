<script lang="ts">
	import type { ToolCall } from '$lib/types';
	import { Wrench, ChevronDown } from '@lucide/svelte';
	import { slide } from 'svelte/transition';

	interface Props {
		toolCall: ToolCall;
	}

	let { toolCall }: Props = $props();
	let open = $state(false);

	const argsJson = $derived(JSON.stringify(toolCall.arguments, null, 2));
	const hasArgs = $derived(Object.keys(toolCall.arguments).length > 0);
</script>

<div data-role="tool-call" class="font-mono text-sm">
	<button
		onclick={() => {
			if (hasArgs) open = !open;
		}}
		class="flex items-center gap-2 text-foreground-muted transition-colors hover:text-foreground {!hasArgs
			? 'cursor-default'
			: 'cursor-pointer'}"
	>
		<Wrench size={14} class="shrink-0 text-primary" />
		<span data-role="tool-name">{toolCall.name}</span>
		{#if hasArgs}
			<ChevronDown
				size={14}
				class="shrink-0 transition-transform duration-200 {open ? 'rotate-180' : ''}"
			/>
		{/if}
	</button>

	{#if open && hasArgs}
		<div transition:slide={{ duration: 150 }}>
			<pre data-role="arguments" class="mt-1 bg-background p-2 text-sm text-foreground"><code
					>{argsJson}</code
				></pre>
		</div>
	{/if}
</div>
