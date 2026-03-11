<script lang="ts">
	import { page } from '$app/state';
	import { resolve } from '$app/paths';
	import type { Pathname } from '$app/types';

	interface BreadcrumbItem {
		label: string;
		href: Pathname;
	}

	interface Props {
		heading: string;
	}

	let { heading }: Props = $props();

	const UUID_REGEX = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

	function formatLabel(segment: string): string {
		if (UUID_REGEX.test(segment)) return segment;
		return segment.replace(/-/g, ' ').replace(/\b\w/g, (c) => c.toUpperCase());
	}

	const breadcrumbs = $derived.by<BreadcrumbItem[]>(() => {
		const segments = page.url.pathname.split('/').filter(Boolean);

		return [
			{ label: 'Home', href: '/' as Pathname },
			...segments.map((segment, i) => ({
				label: formatLabel(segment),
				href: ('/' + segments.slice(0, i + 1).join('/')) as Pathname
			}))
		];
	});
</script>

<header class="mb-2 border-b border-b-muted pb-2">
	<h1 class="font-mono text-xl">{heading}</h1>

	<nav aria-label="Breadcrumb" class="font-mono text-[12px] text-muted-light uppercase">
		<ol class="hidden items-center gap-2 sm:flex">
			{#each breadcrumbs as crumb, i (i)}
				<li>
					{#if i < breadcrumbs.length - 1}
						<a class="transition-colors hover:text-foreground" href={resolve(crumb.href)}
							>{crumb.label}</a
						>
					{:else}
						<span aria-current="page">{crumb.label}</span>
					{/if}

					{#if i < breadcrumbs.length - 1}
						<span aria-hidden="true">/</span>
					{/if}
				</li>
			{/each}
		</ol>
	</nav>
</header>
