<!--
  ToolCard.svelte
  Displays a Tool's name, description, and parameter list.
  Unstyled.
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

<article data-role="tool-card">
	<header>
		<h3 data-role="tool-name">{tool.name}</h3>
		<p data-role="tool-description">{tool.description}</p>
	</header>

	{#if tool.parameters.length > 0}
		<section data-role="parameters">
			{#if required.length > 0}
				<ul data-role="required-params">
					{#each required as param (param.name)}
						<li data-param-name={param.name} data-param-type={param.type}>
							<span data-role="param-name">{param.name}</span>
							<span data-role="param-type"
								>{param.type}{param['element-type'] ? `<${param['element-type']}>` : ''}</span
							>
							<span data-role="required-badge">required</span>
							{#if param.description}
								<p data-role="param-description">{param.description}</p>
							{/if}
							{#if param.enum}
								<ul data-role="enum-values">
									{#each param.enum as val (val)}
										<li>{val}</li>
									{/each}
								</ul>
							{/if}
						</li>
					{/each}
				</ul>
			{/if}

			{#if optional.length > 0}
				<ul data-role="optional-params">
					{#each optional as param (param.name)}
						<li data-param-name={param.name} data-param-type={param.type}>
							<span data-role="param-name">{param.name}</span>
							<span data-role="param-type"
								>{param.type}{param['element-type'] ? `<${param['element-type']}>` : ''}</span
							>
							{#if param.description}
								<p data-role="param-description">{param.description}</p>
							{/if}
							{#if param.enum}
								<ul data-role="enum-values">
									{#each param.enum as val (val)}
										<li>{val}</li>
									{/each}
								</ul>
							{/if}
						</li>
					{/each}
				</ul>
			{/if}
		</section>
	{:else}
		<p data-role="no-params">No parameters</p>
	{/if}
</article>
