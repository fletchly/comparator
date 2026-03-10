<script lang="ts">
	import { PanelLeftClose, PanelLeftOpen } from '@lucide/svelte';
	import { fade } from 'svelte/transition';
	import { resolve } from '$app/paths';
	import type { Pathname } from '$app/types';

	type NavItem = {
		id: string;
		label: string;
		href: Pathname;
		icon?: import('svelte').Component;
	};

	type Props = {
		items: NavItem[];
		activeId?: string;
		collapsed?: boolean;
		brandName?: string;
	};

	let {
		items,
		activeId = $bindable(''),
		collapsed = $bindable(false),
		brandName = 'My App'
	}: Props = $props();

	// Consolidate both MediaQuery effects into one
	let isMobile = $state(false);

	$effect(() => {
		const mq = window.matchMedia('(max-width: 767px)');

		const onChange = (e: MediaQueryListEvent | MediaQueryList) => {
			isMobile = e.matches;
			collapsed = e.matches;
		};

		onChange(mq);
		mq.addEventListener('change', onChange);
		return () => mq.removeEventListener('change', onChange);
	});
</script>

<aside
	data-collapsed={collapsed}
	data-mobile={isMobile}
	class="flex h-full w-64 flex-col transition-[width]
         duration-200 data-[collapsed=true]:w-14
         data-[mobile=true]:fixed data-[mobile=true]:inset-y-0 data-[mobile=true]:left-0 data-[mobile=true]:z-50"
>
	<!-- Header: logo + brand name + toggle -->
	<div class="flex items-center gap-3 overflow-hidden p-2">
		{#if !collapsed}
			<div class="contents" transition:fade={{ duration: 150 }}>
				<!-- Logo placeholder -->
				<span class="block size-8 shrink-0 rounded bg-gray-200"></span>

				<!-- Brand name -->
				<span class="flex-1 overflow-hidden text-sm font-semibold text-ellipsis whitespace-nowrap">
					{brandName}
				</span>
			</div>
		{/if}

		<!-- Toggle button — always visible, styled like a nav icon -->
		<button
			onclick={() => (collapsed = !collapsed)}
			aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
			class="flex size-8 shrink-0 cursor-pointer items-center justify-center rounded"
		>
			{#if collapsed}
				<PanelLeftOpen size={18} />
			{:else}
				<PanelLeftClose size={18} />
			{/if}
		</button>
	</div>

	<!-- Nav items -->
	<nav class="flex flex-col gap-1 overflow-hidden p-2">
		{#each items as item (item.id)}
			<a
				href={resolve(item.href)}
				data-active={activeId === item.id}
				onclick={() => (activeId = item.id)}
				class="flex items-center gap-3 rounded px-2 py-2 whitespace-nowrap
               data-[active=true]:font-semibold"
			>
				{#if item.icon}
					<span class="shrink-0">
						<item.icon size={18} />
					</span>
				{/if}

				{#if !collapsed}
					<span transition:fade={{ duration: 150 }} class="overflow-hidden text-sm text-ellipsis">
						{item.label}
					</span>
				{/if}
			</a>
		{/each}
	</nav>
</aside>
