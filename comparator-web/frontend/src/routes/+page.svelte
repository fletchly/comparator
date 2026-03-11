<script lang="ts">
    import PageHeading from '$lib/components/ui/PageHeading.svelte';
    import Count from '$lib/components/ui/Count.svelte';
    import { toolRuns } from '$lib/stores/toolRuns';
    import type { PageData } from './$types';

    let { data }: { data: PageData } = $props();

    const sessionRuns = $derived($toolRuns.length);
</script>

<PageHeading heading="Dashboard" />

<div class="flex flex-col gap-4 sm:flex-row">
    <Count count={data.conversationCount} label="conversations" href="/conversation" />
    <Count count={data.messageCount} label="messages" href="/conversation" />
    <Count count={sessionRuns} label="tool runs this session" href="/tool" />
    <Count count={data.registeredCount} label="tools registered" href="/tool/registered" />
</div>
