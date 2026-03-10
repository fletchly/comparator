<!--
  ToolCallBadge.svelte
  Shows a single tool call (name + collapsed/expandable arguments).
  Unstyled.
-->
<script lang="ts">
	import type { ToolCall } from '$lib/types';

	interface Props {
		toolCall: ToolCall;
		/** Show arguments by default */
		open?: boolean;
	}

	let { toolCall, open = false }: Props = $props();

	const argsJson = $derived(JSON.stringify(toolCall.arguments, null, 2));
</script>

<details {open}>
	<summary data-role="tool-name">{toolCall.name}</summary>
	<pre data-role="arguments"><code>{argsJson}</code></pre>
</details>
