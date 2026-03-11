<!--
  Count.svelte
  A stat card displaying a large number with a label.
  Renders as an <a> when href is provided.
-->
<script lang="ts">
    interface Props {
        count: number;
        label: string;
        href?: string;
    }

    let { count, label, href }: Props = $props();

    const classes =
        'group flex flex-col gap-1 border border-muted bg-background-secondary px-6 py-5 transition-colors duration-150 hover:border-primary hover:bg-primary-tint w-full';
</script>

{#if href}
    <a {href} data-role="count-card" class={classes}>
        <span class="font-mono text-4xl font-semibold text-primary">{count}</span>
        <span class="font-mono text-xs text-muted-light uppercase transition-colors group-hover:text-foreground">{label}</span>
    </a>
{:else}
    <div data-role="count-card" class={classes}>
        <span class="font-mono text-4xl font-semibold text-primary">{count}</span>
        <span class="font-mono text-xs text-muted-light uppercase transition-colors group-hover:text-foreground">{label}</span>
    </div>
{/if}