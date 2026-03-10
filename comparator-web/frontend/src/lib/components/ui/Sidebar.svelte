<script module lang="ts">
	import type { Pathname } from '$app/types';

	export type NavItem = {
		id: string;
		label: string;
		href: Pathname;
		icon?: import('svelte').Component;
		children?: Omit<NavItem, 'children'>[];
	};
</script>

<script lang="ts">
	import { PanelLeftClose, PanelLeftOpen } from '@lucide/svelte';
	import { fade, slide } from 'svelte/transition';
	import favicon from '$lib/assets/favicon.svg';
	import { resolve } from '$app/paths';

	type Props = {
		items: NavItem[];
		activeId?: string;
		collapsed?: boolean;
		brandName?: string;
		isMobile?: boolean;
	};

	let {
		items,
		activeId = $bindable(''),
		collapsed = $bindable(false),
		brandName = 'Comparator',
		isMobile = $bindable(false)
	}: Props = $props();

	// Initialise synchronously to avoid a collapsed→expanded flash on load
	const _mq = typeof window !== 'undefined' ? window.matchMedia('(max-width: 767px)') : null;
	if (_mq?.matches) {
		isMobile = true;
		collapsed = true;
	}

	$effect(() => {
		const mq = window.matchMedia('(max-width: 767px)');

		const onChange = (e: MediaQueryListEvent | MediaQueryList) => {
			isMobile = e.matches;
			collapsed = e.matches;
		};

		mq.addEventListener('change', onChange);
		return () => mq.removeEventListener('change', onChange);
	});
</script>

<aside
	data-collapsed={collapsed}
	data-mobile={isMobile}
	class="flex h-full w-64 flex-col bg-background-secondary transition-[width]
         duration-200 data-[collapsed=true]:w-14
         data-[mobile=true]:fixed data-[mobile=true]:inset-y-0 data-[mobile=true]:left-0 data-[mobile=true]:z-50"
>
	<!-- Header: logo + brand name + toggle -->
	<div class="flex items-center gap-3 overflow-hidden border-b border-b-muted p-2">
		{#if !collapsed}
			<div
				class="flex flex-1 items-center gap-3 overflow-hidden"
				transition:fade={{ duration: 150 }}
			>
				<!-- Logo -->
				<img src={favicon} alt="Comparator" class="size-6 shrink-0 rounded" />

				<!-- Brand name -->
				<div
					class="translate-y-px overflow-hidden font-mono text-lg leading-none text-ellipsis whitespace-nowrap text-primary uppercase"
				>
					{brandName}
				</div>
			</div>
		{/if}

		<!-- Toggle button — always visible, styled like a nav icon -->
		<button
			onclick={() => (collapsed = !collapsed)}
			aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
			class="flex size-8 shrink-0 cursor-pointer items-center justify-center rounded p-0 leading-none text-foreground-muted transition-colors hover:text-muted-light"
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
				class="flex items-center gap-3 px-2 py-2 whitespace-nowrap text-muted-light
               transition-colors hover:bg-muted hover:text-foreground data-[active=true]:border-l-2 data-[active=true]:border-l-primary data-[active=true]:bg-primary-tint data-[active=true]:text-foreground"
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

			<!-- Child routes: shown when parent or any child is active, and sidebar is expanded -->
			{#if item.children && (activeId === item.id || item.children.some((c) => c.id === activeId)) && !collapsed}
				<div transition:slide={{ duration: 150 }}>
					{#each item.children as child (child.id)}
						<a
							href={resolve(child.href)}
							data-active={activeId === child.id}
							onclick={() => (activeId = child.id)}
							class="flex items-center gap-3 py-2 pr-2 pl-8 whitespace-nowrap text-muted-light
                             transition-colors hover:bg-muted hover:text-foreground data-[active=true]:border-l-2 data-[active=true]:border-l-primary data-[active=true]:bg-primary-tint data-[active=true]:text-foreground"
						>
							{#if child.icon}
								<span class="shrink-0">
									<child.icon size={18} />
								</span>
							{/if}
							{#if !collapsed}
								<span
									transition:fade={{ duration: 150 }}
									class="overflow-hidden text-sm text-ellipsis"
								>
									{child.label}
								</span>
							{/if}
						</a>
					{/each}
				</div>
			{/if}
		{/each}
	</nav>
</aside>
