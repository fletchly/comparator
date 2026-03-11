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

<script lang="ts">
    import { toolRuns } from '$lib/stores/toolRuns';
    import ToolRun from '$lib/components/ToolRun.svelte';
    import PageHeading from '$lib/components/ui/PageHeading.svelte';

    const runs = $derived($toolRuns);
</script>

<PageHeading heading="Tool Runs" />

<div class="mb-4 flex items-center justify-between">
	<span class="font-mono text-sm text-muted-light">
		{runs.length} run{runs.length === 1 ? '' : 's'}
	</span>
</div>

<div class="flex flex-col gap-2">
    {#each runs.slice().reverse() as run (run.id)}
        <ToolRun toolRun={run} />
    {:else}
        <p class="font-mono text-sm text-muted-light">No tool runs yet.</p>
    {/each}
</div>