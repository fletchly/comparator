<script lang="ts">
    import PageHeading from '$lib/components/ui/PageHeading.svelte';
    import ToolCard from '$lib/components/ToolCard.svelte';
    import type { PageData } from './$types';

    let { data }: { data: PageData } = $props();
</script>

<PageHeading heading="Registered Tools" />

<div class="mb-4">
	<span class="font-mono text-sm text-muted-light">
		{data.tools.length} tool{data.tools.length === 1 ? '' : 's'} registered
	</span>
</div>

<div class="flex flex-col gap-2">
    {#each data.tools as tool (tool.name)}
        <ToolCard {tool} />
    {:else}
        <p class="font-mono text-sm text-muted-light">No tools registered.</p>
    {/each}
</div>