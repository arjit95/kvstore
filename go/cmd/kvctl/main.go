package main

import (
	"os"

	"github.com/arjit95/cobi"
	"github.com/arjit95/cobi/editor"
	"github.com/gdamore/tcell"
	"github.com/spf13/cobra"

	"github.com/arjit95/kvstore/go/cmd/kvctl/commands"
	"github.com/arjit95/kvstore/go/cmd/kvctl/factory"
)

var (
	cmd         *cobi.Command
	interactive bool
	kvgate      []string = make([]string, 0)
	f           *factory.Factory
)

func init() {
	cmd = cobi.NewCommand(editor.NewEditor(), &cobra.Command{
		Use:   "kvctl",
		Short: "Cli client to control kvserver",
		Run: func(cmd *cobra.Command, args []string) {

		},
	})

	cmd.PersistentFlags().StringArrayVar(&kvgate, "host", []string{}, "List of kvgate hosts")
	cmd.PersistentFlags().BoolVarP(&interactive, "interactive", "i", false, "Run shell in interactive mode")

	f = factory.CreateFactory()
	cmd.AddCommand(commands.CreateGetCommand(f))
	cmd.AddCommand(commands.CreatePutCommand(f))
}

func main() {
	err := cmd.ParseFlags(os.Args)
	if err != nil {
		cmd.PrintErrf("%s\n", err.Error())
		cmd.Usage()
		os.Exit(1)
	}

	if len(kvgate) == 0 {
		kvgate = append(kvgate, "localhost:3000")
	}

	for _, host := range kvgate {
		f.AddClient(host)
	}

	if interactive {
		cmd.Editor.SetUpperPaneTitle("Kv store")
		cmd.Editor.SetLowerPaneTitle("Logs")
		cmd.Editor.Input.SetFieldBackgroundColor(tcell.ColorBlack)
		cmd.Flag("interactive").Hidden = true
		cmd.ExecuteInteractive()
	} else {
		cmd.Execute()
	}
}
