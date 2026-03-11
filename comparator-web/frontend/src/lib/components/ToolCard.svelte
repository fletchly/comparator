<!--
  ToolCard.svelte
  Displays a Tool's name, description, and parameter list.
-->
<script lang="ts">
	import type { Tool } from '$lib/types';

	interface Props {
		tool: Tool;
	}

	let { tool }: Props = $props();

	const required = $derived(tool.parameters.filter((p) => p.required));
	const optional = $derived(tool.parameters.filter((p) => !p.required));
</script>

<article
		data-role="tool-card"
		class="border border-muted bg-background-secondary px-4 py-3 font-mono"
>
	<header class="mb-2 border-b border-b-muted pb-2">
		<h3 class="text-sm font-semibold text-foreground">{tool.name}</h3>
		<p class="mt-0.5 text-xs text-muted-light">{tool.description}</p>
	</header>

	{#if tool.parameters.length > 0}
		<section data-role="parameters" class="flex flex-col gap-2 pt-1">
			{#each [...required, ...optional] as param (param.name)}
				<div data-role="parameter" class="flex flex-col gap-0.5">
					<div class="flex items-center gap-2">
						<span class="text-xs text-foreground">{param.name}</span>
						<span class="text-[10px] text-primary">{param.type}{param['element-type'] ? `<${param['element-type']}>` : ''}</span>
						{#if param.required}
							<span class="text-[10px] tracking-widest text-secondary uppercase">required</span>
						{/if}
					</div>
					{#if param.description}
						<p class="text-xs text-muted-light">{param.description}</p>
					{/if}
					{#if param.enum}
						<div class="mt-0.5 flex flex-wrap gap-1">
							{#each param.enum as val (val)}
								<span class="border border-muted px-1.5 py-0.5 text-[10px] text-muted-light">{val}</span>
							{/each}
						</div>
					{/if}
				</div>
			{/each}
		</section>
	{:else}
		<p class="text-xs text-muted-light">No parameters.</p>
	{/if}
</article>