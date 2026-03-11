<!--
  StatusBadge.svelte
  Generic status indicator for things like "running", "stopped", "error".
  Unstyled — add class or target data-status with CSS.
-->
<script lang="ts">
	type Status = 'running' | 'stopped' | 'error' | 'unknown';

	interface Props {
		status: Status;
		label?: string;
	}

	let { status, label }: Props = $props();

	const defaultLabels: Record<Status, string> = {
		running: 'Running',
		stopped: 'Stopped',
		error: 'Error',
		unknown: 'Unknown'
	};

	const displayLabel = $derived(label ?? defaultLabels[status]);
</script>

<span data-role="status-badge" data-status={status}>{displayLabel}</span>
