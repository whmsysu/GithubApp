#!/usr/bin/env python3
"""
Script to generate architecture diagrams from Mermaid files
"""

import os
import subprocess
import sys

def install_mermaid_cli():
    """Install mermaid-cli if not already installed"""
    try:
        subprocess.run(['mmdc', '--version'], check=True, capture_output=True)
        print("mermaid-cli is already installed")
    except (subprocess.CalledProcessError, FileNotFoundError):
        print("Installing mermaid-cli...")
        subprocess.run(['npm', 'install', '-g', '@mermaid-js/mermaid-cli'], check=True)

def generate_diagram(mermaid_file, output_file):
    """Generate PNG diagram from Mermaid file"""
    try:
        cmd = ['mmdc', '-i', mermaid_file, '-o', output_file, '-t', 'neutral', '-b', 'white']
        subprocess.run(cmd, check=True)
        print(f"Generated {output_file}")
    except subprocess.CalledProcessError as e:
        print(f"Error generating {output_file}: {e}")

def main():
    # Check if Node.js is installed
    try:
        subprocess.run(['node', '--version'], check=True, capture_output=True)
    except (subprocess.CalledProcessError, FileNotFoundError):
        print("Node.js is not installed. Please install Node.js first.")
        sys.exit(1)

    # Install mermaid-cli
    install_mermaid_cli()

    # Generate diagrams
    diagrams = [
        ('docs/GitHubApp_ComponentDiagram_New.md', 'docs/GitHubApp_ComponentDiagram_New.png'),
        ('docs/GitHubApp_UML_New.md', 'docs/GitHubApp_UML_New.png'),
        ('docs/GitHubApp_UseCase_New.md', 'docs/GitHubApp_UseCase_New.png')
    ]

    for mermaid_file, output_file in diagrams:
        if os.path.exists(mermaid_file):
            generate_diagram(mermaid_file, output_file)
        else:
            print(f"Mermaid file not found: {mermaid_file}")

if __name__ == "__main__":
    main()
