<script lang="ts">
	import type { ButtonVariant } from '$lib/types';

	let { variant = 'primary' as ButtonVariant, children, onclick, disabled = false } = $props();

	const variants: Record<ButtonVariant, string> = {
		primary: 'hover:border-primary hover:text-primary',
		success: 'hover:border-success hover:text-success',
		destructive: 'hover:border-destructive hover:text-destructive'
	};
</script>

<button
	class="cursor-pointer border-2 border-foreground-muted bg-muted px-3 py-1 font-mono text-sm text-muted-light transition-colors duration-200 disabled:pointer-events-none {variants[
		variant
	]}"
	{onclick}
	{disabled}
>
	{@render children()}
</button>
