<!--
  - This file is part of comparator, licensed under the Apache License 2.0
  -
  - Copyright (c) 2026 fletchly <https://github.com/fletchly>
  -
  - Licensed under the Apache License, Version 2.0 (the "License");
  - you may not use this file except in compliance with the License.
  - You may obtain a copy of the License at
  -
  -     http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  - See the License for the specific language governing permissions and
  - limitations under the License.
  -->

<!--
  ToolRun.svelte
  Renders a single tool run entry showing name, args, result, and metadata.
-->

<script lang="ts">
    import type { ToolRun } from '$lib/stores/toolRuns';
    import { Wrench, ChevronDown } from '@lucide/svelte';
    import { slide } from 'svelte/transition';

    interface Props {
        toolRun: ToolRun;
    }

    let { toolRun }: Props = $props();

    let expanded = $state(false);

    const argsJson = $derived(JSON.stringify(toolRun.args, null, 2));
    const hasArgs = $derived(Object.keys(toolRun.args).length > 0);

    const timeLabel = $derived(
        toolRun.timestamp.toLocaleTimeString(undefined, {
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        })
    );
</script>

<article
        data-role="tool-run"
        class="border-l-2 border-l-primary bg-background-secondary px-4 py-3 font-mono"
>
    <header class="flex items-center justify-between gap-2">
        <div class="flex min-w-0 items-center gap-2">
            <Wrench size={14} class="shrink-0 text-primary" />
            <span class="truncate text-sm font-semibold text-primary">{toolRun.name}</span>
        </div>
        <span class="shrink-0 text-[10px] text-muted-light">{timeLabel}</span>
    </header>

    <p class="mt-1 truncate text-xs text-muted-light">
        conv: {toolRun.conversation_id}
    </p>

    {#if hasArgs}
        <button
                onclick={() => (expanded = !expanded)}
                class="mt-2 flex cursor-pointer items-center gap-1 text-xs text-muted-light transition-colors hover:text-foreground"
        >
            <ChevronDown
                    size={13}
                    class="shrink-0 transition-transform duration-200 {expanded ? 'rotate-180' : ''}"
            />
            {expanded ? 'Hide args' : 'Show args'}
        </button>

        {#if expanded}
            <div transition:slide={{ duration: 150 }}>
				<pre
                        data-role="args"
                        class="mt-1 overflow-x-auto bg-background p-2 text-xs text-foreground"><code>{argsJson}</code></pre>
            </div>
        {/if}
    {/if}

    <div class="mt-2 border-t border-t-muted pt-2">
        <span class="text-[10px] tracking-widest text-foreground-muted uppercase">result</span>
        <p class="mt-0.5 text-sm leading-relaxed text-foreground opacity-80">{toolRun.result}</p>
    </div>
</article>
