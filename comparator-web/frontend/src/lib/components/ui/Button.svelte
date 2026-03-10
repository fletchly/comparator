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
	class="border-2 border-primary-foreground bg-muted px-4 py-2 font-mono text-sm text-muted-foreground transition-colors duration-200 disabled:pointer-events-none {variants[
		variant
	]}"
	{onclick}
	{disabled}
>
	{@render children()}
</button>
